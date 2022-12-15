# critter-script
CritterScript is a basic scripting language intended for the Critter competitive simulation.

## Design
CritterScript was designed to be very readable and easy to write for someone without programming experience.
Keywords and constructs are kept to a minimum to reduce the amount of jargon and barriers to learning.
Rather than creating style guidelines, consistency across code is formed naturally because a simple 
syntax only allows a few obvious and correct ways to code something.
CritterScript creates a small and navigable space to explore both the critter simulation and programming as a whole.

## Syntax
CritterScript looks similar to Python on a surface level, but there are differences in syntax.
Here is an example of CritterScript code some important details commented (with `#`):
```js
# Variables are declared with the `var` keyword
var my_steps = 0

# The `global` keyword is used to declare a class or static variable
# Unlike regular variables, this variable shares changes across all instances of this critter
global var all_steps = 0

# Constants are declared with `const`
# Arrays are declared using curly brackets
const FAVORITE_COLOR = {0.5, 0.2, 0.8}
var empty_array = {}

# Instance functions are declared with `method`
# Detail on the built-in and overridable functions are explained below
method start()
    print("hello world")
   
method get_action()
    my_steps = my_steps + 1
    all_steps = all_steps + 1
    
    # Constants are declared implicitly in every CritterScript file to describe common info
    if type_of(FRONT) is ENEMY
        return INFECT
    else if type_of(FRONT) is ALLY and direction_of(FRONT) is not NORTH:
        return TURN_RIGHT
    return HOP 

method get_sprite()
    # CritterScript supports string/array splicing and indexing
    print("awesome"[1:3])
    return "awesome"[my_steps % 7]
    
method get_color()
    return FAVORITE_COLOR

method fun(n, x)
    return n / 2 + x
```

## Overridable Methods

All code written in CritterScript is attached to an instance of a Critter.
While you cannot modify this instance, you can control its behavior with methods.
All the critter's code must be written within methods, except variable declarations.
The user can define their own methods, but the engine will attempt to call the following four during the simulation:

### `get_action()`
Called on every frame that the critter is active.
Intended return type is an integer constant representing the action that the critter should take.
The four actions are constant integers implicitly defined in every CritterScript file.

`TURN_LEFT = 0`, `TURN_RIGHT = 1`, `INFECT = 2`, `HOP = 3`

The user can get information of their surroundings with built-in methods defined [here.](#helperbuilt-in-methods)

### `on_start()`
Called when the critter instance is created. No return value is expected.

### `get_sprite()`
Called when the critter is to be rendered on the screen. 
All critters are rendered as single unicode characters.
The intended return type is a string, but only the first character will be shown on the screen for the critter.

### `get_color()`
Called when the critter is to be rendered on the screen.
This is the color of the critter's text. The intended return type is an array of floating point numbers.
The first three numbers (clamped to fit within the range [0.0, 1.0]) will be interpreted as the magnitude of
red, green, and blue respectively. 

# Helper/built-in Methods

### `type_of(direction)`
Given a direction, returns the type of the critter in that direction relative to this critter.
The four directions are constant integers also implicitly defined in every CritterScript file.

`NORTH = 4`, `EAST = 5`, `SOUTH = 6`, `WEST = 7`

The possible return types of this function are four integer constants.

`ENEMY = 8`, `ALLY = 9`, `WALL = 10`, `EMPTY = 11`

### `direction_of(direction)`
Given a direction, returns the direction of the critter in that direction relative to this critter.
Returns one of the direction constants defined above, or `EMPTY` if the cell is empty.

### `print(variable)`
Prints to the console/output file for debugging.
