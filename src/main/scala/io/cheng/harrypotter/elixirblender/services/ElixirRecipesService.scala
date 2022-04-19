package io.cheng.harrypotter.elixirblender.services

import io.cheng.harrypotter.elixirblender.clients.definitions.{Elixir, Ingredient}
import io.cheng.harrypotter.elixirblender.syntax.elixir._
import scala.collection.mutable
import cats.syntax.all._
import cats.instances.all._

final class ElixirRecipesService() {

  def filterNonBlendableRecipes(recipes: Seq[Elixir]): Seq[Elixir] =
    recipes.filterNot(_.ingredients.isEmpty)

  /** find all elixirs that can be made from provided ingredients by recursively flatten recipes
    *
    * @param recipes
    *   all recipes of elixirs
    * @param ingredients
    *   all provided ingredients
    * @return
    *   a set of elixirs that can be made from provided ingredients
    */
  def findAllBlendableFromIngredientsByFlattenRecipes(
      recipes: Seq[Elixir],
      ingredients: Seq[Ingredient]
  ): Set[Elixir] = {
    val seenRecipes: Set[Elixir] = Set[Elixir]()

    val flattenedRecipes: mutable.Map[Elixir, Set[Ingredient]] =
      mutable.Map[Elixir, Set[Ingredient]]()

    recipes.foldLeft(Set[Elixir]()) { case (aggr, elixir) =>
      flattenRecipesRecursively(elixir, recipes, flattenedRecipes, seenRecipes).fold(aggr) {
        allIngredients =>
          if (allIngredients.subsetOf(ingredients.toSet)) {
            aggr + elixir
          } else {
            aggr
          }
      }
    }
  }

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

  /** recursively flatten elixir recipes into all root-level ingredients. Basically a dfs with
    * memorization(a map contains all flattened recipes)
    *
    * @param elixir
    *   the elixir recipe to flatten
    * @param recipes
    *   all recipes from source
    * @param flattened
    *   all flattened recipes, as a cache of all flattened to avoid repetitive flatten
    * @param seenRecipes
    *   all seen sub-recipes during flattening the elixir's ingredients, used for cycle detection.
    * @return
    *   optional value of Seq[Ingredient]. Return None when cyclic dependency is detected.
    */
  private def flattenRecipesRecursively(
      elixir: Elixir,
      recipes: Seq[Elixir],
      flattened: mutable.Map[Elixir, Set[Ingredient]],
      seenRecipes: Set[Elixir]
  ): Option[Set[Ingredient]] = {
    if (seenRecipes.contains(elixir)) {
      // cycle graph detected, cannot make such elixir
      None
    } else {
      flattened
        .get(elixir)
        .fold[Option[Set[Ingredient]]](
            // never found a flattened recipe
            recipes
              .find(_ == elixir)
              .fold[Option[Set[Ingredient]]](
                  // impossible case
                  None
              )(
                  // try to flatten this recipe
                  _.ingredients.foldLeft(Option(Set.empty[Ingredient])) {
                    case (None, _) =>
                      None // short circuit when cycle detected
                    case (Some(aggr), ingredient) =>
                      recipes
                        .find(_.name == ingredient.name)
                        .fold(
                            // ingredients's name is not found as elixir, which means it is a raw ingredients
                            Option(Set(ingredient))
                        ) { (subRecipe: Elixir) =>
                          flattenRecipesRecursively(
                              subRecipe,
                              recipes,
                              flattened,
                              seenRecipes + subRecipe
                          )
                            .map { allRootIngredients =>
                              flattened.addOne(
                                  subRecipe -> allRootIngredients
                              ) // updating flattened recipes!
                              aggr union allRootIngredients // flatten subrecipe to only root ingredients and add them back
                            }
                        }
                  }
              )
        )(
            // found given elixir in flattened recipes, return as it is
            Some(_)
        )
    }
  }

//  private case class DrugBlenderStats(var cycleDetectedCount: Int, var subRecipeCount: Int)

}
