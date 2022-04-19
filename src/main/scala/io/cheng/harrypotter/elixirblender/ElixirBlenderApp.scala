package io.cheng.harrypotter.elixirblender

import cats.effect.{IO, Resource}
import cats.effect.IOApp.Simple
import io.cheng.harrypotter.elixirblender.clients.Client
import io.cheng.harrypotter.elixirblender.services.ElixirRecipesService
import org.http4s.client.{Client => EmberClient}
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger

object ElixirBlenderApp extends Simple {

  def run: IO[Unit] = {
    val blendService: ElixirRecipesService = new ElixirRecipesService()
    val emberHttpClient: Resource[IO, EmberClient[IO]] = EmberClientBuilder.default[IO].build

    emberHttpClient.use { ember =>
      val wizardWorldClient = Client.httpClient[IO](ember)
      for {
        logger        <- Slf4jLogger.create[IO]
        _             <- logger.info("calling elixir api.")
        elixirRecipes <- wizardWorldClient.getElixirs().map(_.fold(_.toSeq))
        _             <- logger.info(s"got ${elixirRecipes.size} elixir recipes")
        _             <- logger.info("calling ingredients api.")
        ingredients   <- wizardWorldClient.getIngredients().map(_.fold(_.toSeq))
        _             <- logger.info(s"got ${ingredients.size} ingredients")
        filteredRecipes = blendService.filterNonBlendableRecipes(elixirRecipes)
        _ <- logger.info(
            s"after filtering out non-blendable recipes, there are ${filteredRecipes.size} recipes left."
        )
        canBlendSet = blendService.findAllBlendableFromIngredientsByTopologicalSort(
            filteredRecipes,
            ingredients
        )
        _ <- logger.info(
          s"found ${canBlendSet.size} blendable elixir recipes, they are:\n${
            canBlendSet.map(_.name).mkString("[\n\"", "\",\n\"", "\"]")}")
      } yield ()
    }
  }
}
