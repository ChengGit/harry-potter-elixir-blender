package io.cheng.harrypotter.elixirblender.services

import io.cheng.harrypotter.elixirblender.clients.definitions.{Elixir, Ingredient}
import io.cheng.harrypotter.elixirblender.syntax.elixir._
import scala.collection.mutable
import cats.syntax.all._
import cats.instances.all._

final class ElixirRecipesService() {

  def filterNonBlendableRecipes(recipes: Seq[Elixir]): Seq[Elixir] =
    recipes.filterNot(_.ingredients.isEmpty)

  /** find all elixirs that can be made from provided ingredients by topological sort
    *
    * @param recipes
    *   all recipes of elixirs
    * @param ingredients
    *   all provided ingredients
    * @return
    *   a set of elixirs that can be made from provided ingredients
    */
  def findAllBlendableFromIngredientsByTopologicalSort(
      recipes: Seq[Elixir],
      ingredients: Seq[Ingredient]
  ): Set[Elixir] = {

    val elixirIngredientsCountDownMap: mutable.Map[Elixir, Int] =
      populateIngredientsCountDownMap(recipes)

    val ingredientToRecipesMap: mutable.Map[Ingredient, Seq[Elixir]] =
      populateIngredientToRecipesMap(recipes)

    val ingredientQueue = mutable.Queue().addAll(ingredients)

    updateIngredientsCountDownMap(
        elixirIngredientsCountDownMap,
        ingredientToRecipesMap,
        ingredientQueue
    )

    elixirIngredientsCountDownMap.filter(_._2 == 0).keySet.toSet
  }

  private def updateIngredientsCountDownMap(
      elixirIngredientsCountDownMap: mutable.Map[Elixir, Int],
      ingredientToRecipesMap: mutable.Map[Ingredient, Seq[Elixir]],
      ingredientQueue: mutable.Queue[Ingredient]
  ): Unit = {
    while (ingredientQueue.nonEmpty) {
      val ingredient = ingredientQueue.dequeue()
      ingredientToRecipesMap.get(ingredient).foreach {
        _.foreach { e =>
          elixirIngredientsCountDownMap.updateWith(e) {
            case Some(1) =>
              // elixir is now blendable, as all its ingredients are found, and it can be ingredients for other elixirs
              ingredientQueue.addOne(e.toIngredient)
              Some(0)
            case Some(v) if v > 1 =>
              // reduce dependency count by 1
              Some(v - 1)
            case v =>
              // v == Some(0) means elixir is already blendable and added to ingredients queue, do nothing
              // v == None means no such ingredient found in the map, do nothing
              v
          }
        }
      }
    }
  }

  private[services] def populateIngredientToRecipesMap(recipes: Seq[Elixir]) = {
    val ingredientToRecipesMap: mutable.Map[Ingredient, Seq[Elixir]] =
      recipes.foldLeft(mutable.Map[Ingredient, Seq[Elixir]]()) { case (aggrMap, elixir) =>
        elixir.ingredients.foreach(aggrMap.updateWith(_)(_ combine Option(Seq(elixir))))
        aggrMap
      }
    ingredientToRecipesMap
  }

  private[services] def populateIngredientsCountDownMap(recipes: Seq[Elixir]) = {
    val elixirIngredientsCountDownMap: mutable.Map[Elixir, Int] =
      mutable.Map.from(recipes.map(r => r -> r.ingredients.size))
    elixirIngredientsCountDownMap
  }
}
