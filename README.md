# Harry Potter Elixir Blender

This small app fetches elixirs recipes and ingredients, and then figure out what elixirs can be made from given ingredients.

Topological sort is used to solve this challenge. In short, we figure out dependency relationships between ingredients and recipes, 
as well as recipes to recipes, and loop through all ingredients(including sub-recipes) with a queue. 

Within each loop, we deduct amount of dependencies of recipes who depend on an ingredient by 1; Once the amount dependency of a 
recipe is reduced to 0, it means that this recipe can be made from provided ingredients. Also this recipe is added to the aformentioned
queue as an ingredient(sub-recipe) for other recipes.

### Pre-requisites
- You will need to install sbt in order to build a docker image of this app.
Installation guide for [Linux](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html), [MacOS](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
- You will need to install docker. 

### How to Run this mini app
- grant execute access to file `run.sh`
- assuming you are on Linux or MacOS, run `./run.sh`
- application will run and logs are seen from stdout(console)
- application finishes along with docker container

### Assumptions
1. Elixir recipes without ingredients in their recipes cannot be made. Such elixirs won't be in final results.
2. Elixir recipes can depend on other elixirs as ingredients, i.e. there exists intermediate ingredients. 
3. There may be mutual-dependencies: ElixirA needs ElixirB as ingredients while ElixirB needs ElixirA at the same time.
4. All wizardWorld.yaml is created based on my assumptions:
   1. there is no field in the API response hidden when its value is `null`.
   2. all DTO definitions are based on the data from API, assuming those are all possible values.
   3. assuming wizardWorld api won't change(at least not very often), and worldWizard.yaml is valid.
   4. assuming all attributes are `required`, but only some of them are `nullable`. 
      1. This is actually based on a quick analysis of API responses, by counting amount of null from each attribute.
      2. For example, `id` and `name` are assumed `nullable:false` because it has value in every `Elixir` and `Ingredient`.

