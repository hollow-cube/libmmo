## Items
Items are loaded from a JSON data file, the format is described below

### Format
```json5
[
  {
    // Item namespace ID, one for each unique item.
    "namespace": "unnnamed:item", 
    // Numeric ID for this item
    "id": 25,
    // Item material
    //todo might want to decide this based on other factors
    "material": "minecraft:stick",
    
    // Component system
    // Array with one entry for each component. Two components of the same type may not exist at the same time.
    "components": [
      {
        // Component ID
        "type": "unnamed:my_component",
        // All other values are determined by the component requirements.
        // See the section below on implementing components. A few samples are below
      },
      {
        "type": "unnamed:fuel",
        "burn_time": "200"
      },
      {
        "type": "unnamed:sword",
        "cooldown": 5,
        "damage_type": "heavy",
        "base_damage": 25
      },
      {
        "type": "unnamed:rarity",
        "value": "legendary"
      }
    ],
    
    // State system
    // Default state id, at least one must be present and this should be its ID
    "defaultStateId": 100,
    // Only present if there are more than one property, keys and all possible values are listed
    "properties": {
      "key": [
        "true",
        "false",
      ]
    },
    // States must always be present, even with a single state
    "states": {
      // One key for each possible state.
      // If there are no states, there should be one single entry with the key `[]`.
      "[key=true]": {
        // Inside here any properties from the root may be overridden, for example this state will be a blaze rod
        "stateId": 100,
        "material": "minecraft:blaze_rod"
      },
      "[key=false]": {
        "stateId": 101
      },
    }
  }
]
```

### Components
An item component should handle behavior for an item type, be it interaction, lore changes, or anything else.
* Create a class (or record) which implements `Component`
* Create a `Codec<YourComponent>` (typically as a static field on the component class)
  * See https://forge.gemwire.uk/wiki/Codecs
* Create a class which implements `ComponentHandler<YourComponent>`, filling in the relevant methods
* Annotate `YourComponentHandler` with `@AutoService(ComponentHandler.class)`.
* Implement any content methods (`eventNode`, the lore one when added).

### Development
During development, sample items can be added to `development/src/main/resources/data/items.json`.

### Future Notes
Below is a minimal item, except for the commented parts which have not currently been done but probably need to be.
```json5
{
  "unnnamed:item": {
    "id": 25,
    "material": "minecraft:stick",
    
    // Max stack size, probably this should choose which material is used if not specified 
    // eg if stack size is 16 choose egg/ender pearl, another question is whether custom stack 
    // sizes actually makes sense at all given the client predictions issue.
    "stackSize": 32,
    
    "defaultStateId": 100,
    "states": {
      "[]": {
        "stateId": 100
      }
    }
  }
}
```
