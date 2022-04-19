package io.cheng.harrypotter.elixirblender.fixtures

import io.cheng.harrypotter.elixirblender.clients.definitions.{Elixir, ElixirDifficulty, ElixirInventor, Ingredient}
import io.cheng.harrypotter.elixirblender.fixtures.ElixirFixtureBuilder.dummyElixirName

import java.util.UUID

case class ElixirFixtureBuilder(
    id: java.util.UUID = UUID.randomUUID(),
    name: String = dummyElixirName,
    effect: Option[String] = None,
    sideEffects: Option[String] = None,
    characteristics: Option[String] = None,
    time: Option[String] = None,
    difficulty: ElixirDifficulty = ElixirDifficulty.OrdinaryWizardingLevel,
    ingredients: _root_.scala.Vector[Ingredient] = Vector.fill(5)(IngredientFixtureBuilder().build),
    inventors: _root_.scala.Vector[ElixirInventor] = Vector.empty,
    manufacturer: Option[String] = None
) {
  def build: Elixir = Elixir(
      id = id,
      name = name,
      effect = effect,
      sideEffects = sideEffects,
      characteristics = characteristics,
      time = time,
      difficulty = difficulty,
      ingredients = ingredients,
      inventors = inventors,
      manufacturer = manufacturer
  )
}

object ElixirFixtureBuilder {
  val dummyElixirName = "dummyElixir"
}
