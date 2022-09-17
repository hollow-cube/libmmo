# Minecraft Query Language (mql)

a.k.a MoLang Jr.

A subset of MoLang (may eventually be a full implementation). Currently implemented as a basic tree-walk interpreter, 
but may eventually be refactored to something more performant in the future. 

## Syntax

`mql` supports the following syntax
* [Query functions](#query-functions)

## Query Functions

`mql` implements a subset of the query functions in MoLang. They are described below.

| Function      | Status | Description                                                                                                |
|---------------|--------|------------------------------------------------------------------------------------------------------------|
| q.time_of_day | +      | Gets the current time of day in the world as a decimal: midnight=0.0, sunrise=0.25, noon=0.5, sunset=0.75. |
| q.has_target  | +      | Returns true if the entity has a target, false otherwise.                                                  |
| q.is_alive    | +      | Returns true if the entity is alive, false otherwise                                                       |
|               |        |                                                                                                            |
|               |        |                                                                                                            |
