package ironlog.routes

import ironlog.{Auth, Config}
import ironlog.Middleware.{ok, err}
import com.stripe.Stripe
import com.stripe.model.checkout.{Session => StripeSession}
import com.stripe.param.checkout.SessionCreateParams

object DonateRoutes extends cask.Routes:

  @cask.post("/api/donate/session")
  def createSession(request: cask.Request) =
    try
      Auth.requireUser(request)
      val body     = ujson.read(request.text())
      val amountEur = body("amount").num.toLong  // euros
      if amountEur < 1 || amountEur > 500 then
        return err("Amount must be between 1 and 500", 400)

      if Config.stripeSecretKey.isEmpty then
        return err("Stripe is not configured", 503)

      Stripe.apiKey = Config.stripeSecretKey

      val params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setSuccessUrl(s"${Config.frontendUrl}/app/donate/success")
        .setCancelUrl(s"${Config.frontendUrl}/app/donate")
        .addLineItem(
          SessionCreateParams.LineItem.builder()
            .setQuantity(1L)
            .setPriceData(
              SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("eur")
                .setUnitAmount(amountEur * 100L) // cents
                .setProductData(
                  SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Iron Log Donation")
                    .setDescription("Thanks for supporting Iron Log!")
                    .build()
                )
                .build()
            )
            .build()
        )
        .build()

      val session = StripeSession.create(params)
      ok(ujson.Obj("url" -> session.getUrl))
    catch case e: Exception => err(e.getMessage, 500)

  initialize()
