# Life-Size Bdubs
## Befriend your own life-size Bdubs!
#### Made for Modfest 1.21!
Ever get tired of being by yourself? Ever wish you could have your own little BDoubleO100(Bdubs) friend? Well now you can!

## Good Times With Bdubs
Simply give Bdubs a clock, and he'll do anything for you!  
But clocks aren't the only items you can give Bdubs. Giving him a moss block gives him a mossy costume, and giving him a tnt block... well, he becomes slightly unhinged.

Upon giving a Bdubs an item, that Bdubs becomes yours! You can now right-click him to put him on your shoulder! Bdubs tells you little fun facts and quotes while on your shoulder! How fun? (The messages sent from Bdubs on your shoulder can be disabled in the client config.)

_Notice: Giving sugar to Bdubs may result in unexpected consequences. You have been warned!_

Additional items can also be given for different variants.
- A ladder for Etho
- An enchanting table for GoodTimesWithScar
- An egg for Grian
- A wind charge for Skizzleman
- A saddle for SolidarityGaming

Bdubs doesn't want to cause lag on your world though, so after 5 minutes of no interaction from the Bdubs owner, he'll disappear without a trace!

**_Note:_** As of Life-Size Bdubs v1.0.0, there is no way of finding a Bdubs entity in survival. He can only be spawned in creative. This may change in the future if I can find a nice vanilla-friendly way to implement it.

## Shoulder Entity Locking
If there is an entity on your shoulder, right-clicking a slime ball will toggle the newly added shoulder entity locking. If your shoulder entities are locked, they cannot be removed from your shoulder under most circumstances.

This was done so that you can jump and fly around with Bdubs on your shoulders without needing to worry about him falling off! Do note that there are still ways for them to fall off, like changing gamemodes and dying.

## Data Driven Variants
It turns out Bdubs taught data how to drive! Additional Bdubs variants can be added thanks using datapacks. It's quite simple really - so simple even Scar could do it(probably)!

The texture is the exact same as a Minecraft skin, so you can simply copy-paste a skin to add your own variant!

## Extras & Special Thanks

This mod was a mod created by a fan of HermitCraft(and friends of the Hermits), and means no harm towards any of the creators mentioned. It was originally a reference to the one bit ZombieCleo did where she made a tiny armor stand of Bdubs.

While all the skin textures were recreated by me to better fit the smaller entity and my art style, it is in no means supposed to undermine the work, creativity, and originality of the creator's original skins and their respective artist. 

All the creator's mentioned via built-in Bdubs variants can be found here:
- BDoubleO100 (referred to as Bdubs): https://www.youtube.com/@bdoubleo
- EthosLab (referred to as Etho): https://www.youtube.com/@EthosLab
- Grian: https://www.youtube.com/@Grian
- GoodTimesWithScar (referred to as Scar): https://www.youtube.com/@GoodTimesWithScar
- Skizzleman (referred to as Skizz): https://www.youtube.com/@MCSkizzleman
- SolidarityGaming (referred to as Timmy): https://www.youtube.com/@SolidarityGaming

## Dependencies & Compat

GeckoLib v4 is required.

As of Life-Size Bdubs v1.0.0, there may be an incompatibility with Ears skins that have wings enabled. There will be an attempt to fix this in the future, but no guarantees.

**Have fun!!**

---

## Adding your own life-size friend!

### Template Json (WIP)
Replace all `examplepack` with your datapack name, and all `examplebdubs` with your Bdubs variant name.

The json should be found in:  
`data/examplepack/lifesizebdubs/bdubs_variant/examplebdubs.json`

The texture should be found in:
`data/examplepack/bdubs/examplebdubs.png`

```json templatebdubs.json
{
  "name": {
    "translate": "<pack name here>.variant.<variant name here>"
  },
  "item": "minecraft:clock",
  "texture": "<pack name here>:textures/bdubs/<variant name here>.png",
  "messages": [
    "<message one>",
    "<message two>"
  ],
  "timed_messages": [
    {
      "msg": "<time message>",
      "time": 12500
    }
  ]
}
```

### Json explained
- name: The name shown when the Bdubs variant sends a messages and dies (Text component).
- item: The item needed to give a Bdubs entity to turn it into that variant (Item).
- texture: The texture used for the Bdubs variant (Resource Location).
- messages: The list of possible messages the Bdubs variant can send once on their owner's shoulder (String array)
- timed_messages: A list of messages to be sent at a specific time to the owner if the Bdubs entity is on their shoulder (Array of Pair of String and Int).
  - msg: The message sent at the specific time (String)
  - time: The time of day the message of `msg` should be sent. (Int 0-24000)

### Adding your own variant (WIP)

Adding your own Bdubs variant can be done with a datapack. This page does not tell you how to create a datapack.

For this example, we will be adding a new variant called "Booga". Let's begin! 

#### Add your skin texture

Add a Minecraft skin texture in:  
`data/<your pack name here>/bdubs/booga.png`

#### Add your json file

Create a new json file in:  
`data/<your pack name here>/lifesizebdubs/bdubs_variant/booga.json`

Copy-paste the template above into that newly created json file.

Change all the instances of `<pack name here>` and `<variant name here>` to your pack name and variant name respectively.

For this example, the pack name is `boogapack`

```json booga.json
{
  "name": "Mini Booga",
  "item": "minecraft:diamond",
  "texture": "boogapack:textures/bdubs/booga.png",
  "messages": [
    "Booga 1",
    "Booga 2 electric boogaloo"
  ],
  "timed_messages": [
    {
      "msg": "Timed booga",
      "time": 12500
    }
  ]
}
```

And that's it! Load up the game and try giving your new variant the item you defined in your json(a diamond in this case).

You can add as many messages as you like too. Keeping the `messages` array blank means no messages will be sent. Not including `timed_messages` at all means there will be no timed messages.