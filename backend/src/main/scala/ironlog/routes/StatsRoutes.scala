package ironlog.routes

import ironlog.*
import ironlog.model.*
import ironlog.Middleware.*
import scalasql.PostgresDialect.*
import java.time.LocalDate

object StatsRoutes extends cask.Routes:

  @cask.get("/api/stats/summary")
  def summary(request: cask.Request) = handleAuth(request) { userId =>
    try
      val now       = LocalDate.now()
      val weekStart = now.minusDays(now.getDayOfWeek.getValue - 1)

      val allWorkouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val weekWorkouts = allWorkouts.filter(w => !w.startedAt.toLocalDate.isBefore(weekStart))
      val workoutIds   = allWorkouts.map(_.id).toSet
      val weekIds      = weekWorkouts.map(_.id).toSet

      val allSets = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
        .filter(s => workoutIds.contains(s.workoutId))
      val weekSets = allSets.filter(s => weekIds.contains(s.workoutId))

      val weeklyVolume  = weekSets.flatMap(s => for wt <- s.weightKg; r <- s.reps yield wt * r).sum
      val weeklyMinutes = weekWorkouts.flatMap(w =>
        w.endedAt.map(e => java.time.Duration.between(w.startedAt, e).toMinutes)
      ).sum

      val completedDates = allWorkouts.map(_.startedAt.toLocalDate).toSet
      var streak = 0
      var day    = now
      while completedDates.contains(day) do { streak += 1; day = day.minusDays(1) }

      ok(ujson.Obj(
        "weeklyVolume"   -> weeklyVolume,
        "weeklyWorkouts" -> weekWorkouts.length,
        "weeklyMinutes"  -> weeklyMinutes,
        "streak"         -> streak,
        "totalWorkouts"  -> allWorkouts.length,
      ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/stats/volume")
  def volume(period: String = "weekly", count: Int = 12, request: cask.Request) = handleAuth(request) { userId =>
    try

      val workouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val workoutById = workouts.map(w => w.id -> w).toMap
      val allSets = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
        .filter(s => workoutById.contains(s.workoutId))

      val setsWithLabel = allSets.flatMap { s =>
        workoutById.get(s.workoutId).map { w =>
          val d = w.startedAt.toLocalDate
          val label = if period == "monthly" then d.withDayOfMonth(1).toString
                      else d.minusDays(d.getDayOfWeek.getValue - 1).toString
          (label, s)
        }
      }

      val grouped = setsWithLabel.groupBy(_._1).map { (label, pairs) =>
        val vol = pairs.flatMap { (_, s) => for wt <- s.weightKg; r <- s.reps yield wt * r }.sum
        ujson.Obj("label" -> label, "volume" -> vol)
      }.toSeq.sortBy(_("label").str).takeRight(count)

      ok(ujson.Arr(grouped*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/stats/prs")
  def prs(request: cask.Request) = handleAuth(request) { userId =>
    try
      val workouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val workoutById = workouts.map(w => w.id -> w).toMap
      val allSets = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
        .filter(s => workoutById.contains(s.workoutId))
      val exercises  = Database.run(_.run(Exercise.select))
      val exById     = exercises.map(e => e.id -> e).toMap

      val prList = allSets.groupBy(_.exerciseId).toSeq.flatMap { (exId, sets) =>
        exById.get(exId).flatMap { ex =>
          val pts = sets.flatMap { s =>
            for wt <- s.weightKg; r <- s.reps if r > 0
            yield (wt, r, wt * (1 + r / 30.0), workoutById.get(s.workoutId).map(_.startedAt.toLocalDate.toString).getOrElse(""))
          }
          pts.maxByOption(_._3).map { best =>
            ujson.Obj("exerciseName" -> ex.name, "weightKg" -> best._1,
                      "reps" -> best._2, "estimatedOneRm" -> best._3, "date" -> best._4)
          }
        }
      }
      ok(ujson.Arr(prList*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/stats/progress/:exerciseId")
  def progress(exerciseId: String, request: cask.Request) = handleAuth(request) { userId =>
    try
      val workouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val workoutById = workouts.map(w => w.id -> w).toMap
      val sets = Database.run(_.run(
        WorkoutSet.select.filter(s => s.exerciseId === exerciseId && s.weightKg.isDefined && s.reps.isDefined)
      )).filter(s => workoutById.contains(s.workoutId))

      val points = sets.groupBy(s => workoutById(s.workoutId).startedAt.toLocalDate.toString).toSeq.flatMap { (date, ds) =>
        val pts = ds.flatMap { s => for wt <- s.weightKg; r <- s.reps if r > 0 yield (wt, r, wt * (1 + r / 30.0)) }
        pts.maxByOption(_._3).map { best =>
          ujson.Obj("date" -> date, "estimatedOneRm" -> best._3, "bestWeightKg" -> best._1, "bestReps" -> best._2)
        }
      }.sortBy(_("date").str)

      ok(ujson.Arr(points*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/stats/frequency")
  def frequency(days: Int = 365, request: cask.Request) = handleAuth(request) { userId =>
    try
      val cutoff = LocalDate.now().minusDays(days)

      val workouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      )).filter(w => !w.startedAt.toLocalDate.isBefore(cutoff))

      val workoutById = workouts.map(w => w.id -> w).toMap
      val allSets = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
        .filter(s => workoutById.contains(s.workoutId))

      val volByWorkout = allSets.groupBy(_.workoutId).map { (wId, ss) =>
        wId -> ss.flatMap(s => for wt <- s.weightKg; r <- s.reps yield wt * r).sum
      }

      val points = workouts.groupBy(_.startedAt.toLocalDate.toString).map { (date, ws) =>
        val vol = ws.map(w => volByWorkout.getOrElse(w.id, 0.0)).sum
        ujson.Obj("date" -> date, "volume" -> vol, "workoutCount" -> ws.length)
      }.toSeq.sortBy(_("date").str)

      ok(ujson.Arr(points*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.get("/api/stats/muscle-breakdown")
  def muscleBreakdown(period: String = "weekly", count: Int = 12, from: String = "", to: String = "", request: cask.Request) = handleAuth(request) { userId =>
    try
      val allWorkouts = Database.run(_.run(
        Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
      ))
      val workouts =
        if from.nonEmpty && to.nonEmpty then
          val fromDate = LocalDate.parse(from)
          val toDate   = LocalDate.parse(to)
          allWorkouts.filter(w => !w.startedAt.toLocalDate.isBefore(fromDate) && !w.startedAt.toLocalDate.isAfter(toDate))
        else
          val cutoff = if period == "monthly" then LocalDate.now().minusMonths(count)
                       else LocalDate.now().minusWeeks(count)
          allWorkouts.filter(w => !w.startedAt.toLocalDate.isBefore(cutoff))

      val workoutIds = workouts.map(_.id).toSet
      val sets = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
        .filter(s => workoutIds.contains(s.workoutId))
      val exById = Database.run(_.run(Exercise.select)).map(e => e.id -> e).toMap

      val byGroup = sets.groupBy(s => exById.get(s.exerciseId).map(_.muscleGroup).getOrElse("Other"))
      val volumes = byGroup.map { (g, ss) => g -> ss.flatMap(s => for wt <- s.weightKg; r <- s.reps yield wt * r).sum }
      val total   = volumes.values.sum

      val breakdown = volumes.map { (g, vol) =>
        ujson.Obj("muscleGroup" -> g, "volumeKg" -> vol, "percentage" -> (if total > 0 then vol / total * 100 else 0.0))
      }.toSeq

      ok(ujson.Arr(breakdown*))
    catch case e: Exception => err(e.getMessage, 500)
  }

  @cask.post("/api/stats/analyse")
  def analyse(request: cask.Request) = handleAuth(request) { userId =>
    try
      if Config.anthropicKey.isEmpty then
        err("ANTHROPIC_API_KEY is not configured", 500)
      else
        val cutoff = LocalDate.now().minusWeeks(8)

        val workouts = Database.run(_.run(
          Workout.select.filter(w => w.userId === userId && w.endedAt.isDefined)
        )).filter(w => !w.startedAt.toLocalDate.isBefore(cutoff))
          .sortBy(_.startedAt.toString)

        val workoutIds  = workouts.map(_.id).toSet
        val allSets     = Database.run(_.run(WorkoutSet.select.filter(s => s.weightKg.isDefined && s.reps.isDefined)))
          .filter(s => workoutIds.contains(s.workoutId))
        val exercises   = Database.run(_.run(Exercise.select))
        val exById      = exercises.map(e => e.id -> e).toMap
        val setsByWorkout = allSets.groupBy(_.workoutId)

        // Build a compact summary for the prompt
        val workoutLines = workouts.map { w =>
          val durationMin = w.endedAt.map(e => java.time.Duration.between(w.startedAt, e).toMinutes).getOrElse(0L)
          val sets = setsByWorkout.getOrElse(w.id, Seq.empty)
          val volume = sets.flatMap(s => for wt <- s.weightKg; r <- s.reps yield wt * r).sum
          val muscles = sets.flatMap(s => exById.get(s.exerciseId).map(_.muscleGroup)).distinct.mkString(", ")
          val setLines = sets.groupBy(_.exerciseId).map { (exId, ss) =>
            val name = exById.get(exId).map(_.name).getOrElse("Unknown")
            val summary = ss.map(s => s"${s.weightKg.getOrElse(0)}kg×${s.reps.getOrElse(0)}").mkString(", ")
            s"    $name: $summary"
          }.mkString("\n")
          s"- ${w.startedAt.toLocalDate} | ${w.title} | ${durationMin}min | ${volume.toInt}kg total | muscles: $muscles\n$setLines"
        }.mkString("\n\n")

        // Muscle group volume breakdown
        val muscleVolumes = allSets.groupBy(s => exById.get(s.exerciseId).map(_.muscleGroup).getOrElse("Other"))
          .map { (g, ss) => g -> ss.flatMap(s => for wt <- s.weightKg; r <- s.reps yield wt * r).sum.toInt }
          .toSeq.sortBy(-_._2)
          .map { (g, vol) => s"  $g: ${vol}kg" }.mkString("\n")

        val totalWorkouts = workouts.length
        val avgPerWeek    = totalWorkouts / 8.0

        val prompt = s"""You are a professional strength and conditioning coach. Analyse the following 8-week workout log and write a personalised ~1-page report for this athlete.

The report should cover:
1. A brief summary of their training habits (frequency, consistency, volume)
2. Muscle group balance — what they are overdoing or neglecting
3. Strength trends — are they progressing, plateauing, or regressing on key lifts?
4. Specific actionable suggestions (e.g. add more X, reduce Y, try Z technique)
5. A recommended weekly workout structure going forward

Keep the tone encouraging but honest. Use clear sections with short headers. Be specific — reference actual exercises and numbers from the data.

--- WORKOUT DATA (last 8 weeks) ---
Total sessions: $totalWorkouts (~${f"$avgPerWeek%.1f"}/week)

Volume by muscle group:
$muscleVolumes

Sessions:
$workoutLines
---

Write the report now:"""

        val response = requests.post(
          "https://api.anthropic.com/v1/messages",
          readTimeout = 60000,
          connectTimeout = 10000,
          headers = Map(
            "x-api-key"         -> Config.anthropicKey,
            "anthropic-version" -> "2023-06-01",
            "content-type"      -> "application/json",
          ),
          data = ujson.Obj(
            "model"      -> "claude-haiku-4-5-20251001",
            "max_tokens" -> 1024,
            "messages"   -> ujson.Arr(ujson.Obj(
              "role"    -> "user",
              "content" -> prompt,
            )),
          ).toString,
        )
        val body    = ujson.read(response.text())
        val content = body("content")(0)("text").str

        // Generate a title from the date range
        val dateLabel = s"Report – ${LocalDate.now()}"
        val reportId  = java.util.UUID.randomUUID().toString
        val now       = java.time.LocalDateTime.now()
        Database.run(_.run(Report.insert.columns(
          _.id        := reportId,
          _.userId    := userId,
          _.title     := dateLabel,
          _.content   := content,
          _.createdAt := now,
        )))

        ok(ujson.Obj(
          "id"        -> reportId,
          "title"     -> dateLabel,
          "content"   -> content,
          "createdAt" -> now.toString,
        ))
    catch case e: Exception => err(e.getMessage, 500)
  }

  initialize()
