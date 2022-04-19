package io.cheng.harrypotter.elixirblender

import io.cheng.harrypotter.elixirblender.clients.definitions.{Elixir, Ingredient}
import io.cheng.harrypotter.elixirblender.fixtures.{ElixirFixtureBuilder, IngredientFixtureBuilder}
import io.cheng.harrypotter.elixirblender.services.ElixirRecipesService
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.cheng.harrypotter.elixirblender.syntax.elixir._

class ElixirRecipesServiceSpec extends AnyFlatSpec with Matchers with ElixirRecipesServiceFixture {
  val service = new ElixirRecipesService()

  it should "filter out elixir recipes that have 0 ingredients" in {
    service.filterNonBlendableRecipes(
        Seq(allBaseIngredientsElixir1, noIngredientElixir)
    ) should contain theSameElementsAs (Seq(allBaseIngredientsElixir1))
  }

  it should "find elixirs whose ingredients are in provided ingredient lists" in {
    service.findAllBlendableFromIngredients(
        Seq(allBaseIngredientsElixir1, allBaseIngredientsElixir2),
        Seq(baseIngredient1, baseIngredient2, baseIngredient3)
    ) should contain theSameElementsAs Seq(allBaseIngredientsElixir1, allBaseIngredientsElixir2)
  }

  it should "find elixirs whose ingredients are have both base ingredients and sub-recipes" in {
    service.findAllBlendableFromIngredients(
        Seq(elixirRequiringIntermediateElixir, intermediateElixir),
        Seq(baseIngredient1, baseIngredient2, baseIngredient3)
    )
  }

  it should "not blend any elixir with missing ingredients" in {
    service.findAllBlendableFromIngredients(
        Seq(elixirRequiringMissingIngredient),
        Seq(baseIngredient1, baseIngredient2, baseIngredient3)
    ) shouldBe empty
  }
}

trait ElixirRecipesServiceFixture {
  val baseIngredientName1         = "i1"
  val baseIngredientName2         = "i2"
  val baseIngredientName3         = "i3"
  val missingIngredientName       = "missing"
  val baseIngredient1: Ingredient = new IngredientFixtureBuilder(name = baseIngredientName1).build
  val baseIngredient2: Ingredient = new IngredientFixtureBuilder(name = baseIngredientName2).build
  val baseIngredient3: Ingredient = new IngredientFixtureBuilder(name = baseIngredientName3).build
  val aMissingIngredient: Ingredient = new IngredientFixtureBuilder(
      name = missingIngredientName
  ).build

  val noIngredientElixir: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector[Ingredient]()
  ).build

  val allBaseIngredientsElixir1: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector(baseIngredient1, baseIngredient2, baseIngredient3)
  ).build

  val allBaseIngredientsElixir2: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector(baseIngredient2, baseIngredient3)
  ).build

  val elixirRequiringMissingIngredient: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector(baseIngredient1, aMissingIngredient)
  ).build

  val intermediateElixir: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector(baseIngredient1)
  ).build

  val elixirRequiringIntermediateElixir: Elixir = new ElixirFixtureBuilder(
      ingredients = Vector(baseIngredient1, baseIngredient2, intermediateElixir.toIngredient)
  ).build

//  val allBaseIngredientsElixir3: Elixir = new ElixirFixtureBuilder(
//    ingredients = Vector(baseIngredient1, baseIngredient2, baseIngredient3)
//  ).build

}
