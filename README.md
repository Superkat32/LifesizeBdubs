# Life Size Bdubs
## Befriend your own life size Bdubs!
#### Made for Modfest 1.21!
Ever get tired of being by yourself? Ever wish you could have your own little BDoubleO100 friend? Well now you can!

## Adding your own life size friend!
Adding your own life size friend is so easy, even Scar could do it(probably)!

#### Add your own texture
Likely in the path `data/<datapack name>/bdubs/<variant texture name>.png`

#### Adding the json file

In file path 
``data/<datapack name>/lifesizebdubs/bdubs_variant/<your variant name>.json``

```json <your bdubs variant name>.json
{
  "name": "<variant name here (e.g. bdubs)>",
  "item": "<item here (e.g. minecraft:clock)>",
  "texture": "<texture here (your added texture path from earlier)",
  "messages": [
    "<message 1>",
    "<message 2>"
  ],
  "timed_messages": [
    {
      "msg": "<message>",
      "time": <time of day> (anywhere from 0 through 24000 - e.g. 12500)
    }
  ]
}
```