package io.cheng.harrypotter.elixirblender.syntax

import io.cheng.harrypotter.elixirblender.clients.definitions.{Elixir, Ingredient}

import scala.language.implicitConversions

trait ElixirAsIngredientSyntax {
  implicit def toIngredient(elixir: Elixir): ElixirAsIngredientExtension =
    new ElixirAsIngredientExtension(elixir)

  protected class ElixirAsIngredientExtension(elixir: Elixir) {
    def toIngredient: Ingredient = Ingredient(elixir.id, elixir.name)
  }
}

object elixir extends ElixirAsIngredientSyntax
