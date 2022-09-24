# Entities
Module for defining entities as well as their associated behavior.

## Entity Definition
```json5
{
    "namespace": "unnamed:test",
    
    // The model to use. For now this must be a vanilla entity type.
    "model": "minecraft:pig",
    // A reference to a behavior file
    "behavior": "unnamed:test_behavior",
    // (optional) The navigator to use for this entity. If not specified, the land navigator will be used.
    "navigator": "minecraft:land",
    
    // (optional) The loot table to use when the entity dies
    "loot_table": "unnamed:test_loot",
    
    // Stats
    //todo need to be able to specify things like walk speed, jump height, etc. These are navigator parameters i suppose
}
```

## Behavior Definition
```json5
{
    "namespace": "test_behavior",
    
    // Root behavior node
    "type": "sequence",
    "children": [/* ... */],
    // (optional) If true, the task may be interrupted during execution by a parent sequence. Defaults to false.
    "canInterrupt": false,
}
```

A behavior node is a JSON object with a type and some set of properties defined on the node itself. The basic primitives
are `sequence`s and `selector`s. They are documented below:

```json5
{
    // Sequence is a set of tasks to perform in order. If any task fails, the sequence fails.
    "type": "unnamed:sequence",
    // Each child is executed in order. If the sequence may be interrupted then any child task may be interrupted. 
    "children": [/* ... */],
}
```

```json5
{
    // Selector is a set of tasks which will be performed based on their order and condition.
    "type": "unnamed:selector",
    // An array of stimuli definitions which will be active as long as this selector is active. This can be used for
    // performance reasons (e.g. do not tick a stimuli source if you do not have to), but should not be used if there
    // are two conflicting stimuli sources (e.g. do not have two targeting stimuli nested within each other).
    "stimuli": [/* ... */],
    // A set of mql expressions and tasks. Each expression is evaluated in order, executing the first task to completion.
    // If the selected task may not be interrupted, it is executed to completion. If it may be interrupted, the
    // conditions will be continuously evaluated, and the selected task will change if an earlier condition passes.
    // An empty mql expression will always evaluate to true.
    "children": {
        "q.has_target": {/* ... */},
        "": {/* ... */},
    }
}
```
