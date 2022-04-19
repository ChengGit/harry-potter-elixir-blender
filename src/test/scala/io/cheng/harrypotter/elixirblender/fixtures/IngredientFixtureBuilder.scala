package io.cheng.harrypotter.elixirblender.fixtures

import io.cheng.harrypotter.elixirblender.clients.definitions.Ingredient
import io.cheng.harrypotter.elixirblender.fixtures.IngredientFixtureBuilder.dummyIngredientName

import java.util.UUID

case class IngredientFixtureBuilder(id: UUID = UUID.randomUUID(), name: String = dummyIngredientName) {
  def build: Ingredient = Ingredient(id = id, name = name)
}

object IngredientFixtureBuilder {
  val dummyIngredientName = "ingredient"
}
