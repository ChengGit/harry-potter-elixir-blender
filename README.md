# Harry Potter Elixir Blender

This small app fetches elixirs recipes and ingredients, and then figure out what elixirs can be made from given ingredients.

Topological sort is used to solve this challenge. In short, we figure out dependency relationships between ingredients and recipes, 
as well as recipes to recipes, and loop through all ingredients(including sub-recipes) with a queue. 

Within each loop, we deduct amount of dependencies of recipes who depend on an ingredient by 1; Once the amount dependency of a 
recipe is reduced to 0, it means that this recipe can be made from provided ingredients. Also this recipe is added to the aformentioned
queue as an ingredient(sub-recipe) for other recipes.

## Analysis
When looking into the domain objects(elixir and ingredients) and their relationship, I realise this is kinda a graph-based challenge, since 
elixir recipes depends on ingredients. Thinking it twice, I realise that there may be intermediate ingredients(sub-recipes) that is dependent 
by other recipes, so cool we need to think about multi-hop routes from ingredients to a final recipe. 
What's more, if we assume a recipe can depend on another, it introduces cycles in the graph. Cycles can leads to infinite loops if 
not handled properly.

## Algorithm(s)
I had 2 solutions in mind, one was an intuitive recursion solution and the other one was topological sort.
### Recursion Solution
Initially I implemented a recursion solution: 
- I needed to recursively drill into each recipe and flatten sub-recipes into only root ingredients.
- I also needed to detect cycle and short-circuit.

I had it implemented and checked-in in the first commit. I removed it as I felt my next solution was easir to follow and read.

### Topological Sort Solution
Topological sort can help to figure out one way of doing tasks in sequence. That's exactly what I need:
I want to figure out if all the dependent ingredients and sub-recipes available for a given elixir recipe.

Rewinding [this MIT open courseware video](https://www.youtube.com/watch?v=AfSk24UTFS8&ab_channel=MITOpenCourseWare) and a bunch of other 
tutorials/articles helped me to recall implementation details. It did require some preparations before looping through ingredients.
- We counted amount of dependencies of each recipe
- We built an ingredients/sub-recipes to recipes map
- We loop through all ingredients, with help of a queue. Using queue instead of Array is that we need to put ready-to-blend sub-recipes into this queue along the loop.
Queue is the right tool to represent an array which changes size dynamically during looping.
- For each ingredient in the queue, we deduct dependency count of recipes depending on this ingredient by 1. 
- When a dependency counter of a recipe is reduced to 0, it is ready to be blended, and can be considered as a potential ingredient.
- We submit such recipe as ingredient into queue.
- When queue is drained, we have gone through all ingredients and sub-recipes, so we get all recipes with dependency counter == 0 as final result. 



## How to Run this mini app
### Pre-requisites
- You will need to install sbt in order to build a docker image of this app.
  Installation guide for [Linux](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html), [MacOS](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Mac.html)
- You will need to install docker.
### Steps
- grant execute access to file `chmod +x ./run.sh`
- assuming you are on Linux or MacOS, run `./run.sh`
- application will run and logs are seen from stdout(console)
- application terminates along with docker container

## Assumptions
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

