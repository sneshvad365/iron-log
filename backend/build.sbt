scalaVersion := "3.5.0"

name := "iron-log-backend"

libraryDependencies ++= Seq(
  "com.lihaoyi"    %% "cask"       % "0.9.2",
  "com.lihaoyi"    %% "scalasql"   % "0.1.4",
  "com.lihaoyi"    %% "upickle"    % "3.3.1",
  "com.lihaoyi"    %% "requests"   % "0.8.3",
  "org.postgresql"  % "postgresql" % "42.7.3",
  "at.favre.lib"    % "bcrypt"     % "0.10.2",
  "com.auth0"       % "java-jwt"   % "4.4.0",
  "com.stripe"      % "stripe-java" % "24.3.0",
)

Compile / mainClass := Some("ironlog.Main")
