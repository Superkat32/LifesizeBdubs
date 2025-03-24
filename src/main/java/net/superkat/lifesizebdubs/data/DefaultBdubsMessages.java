package net.superkat.lifesizebdubs.data;

import net.minecraft.text.Text;

import java.util.List;

public class DefaultBdubsMessages {

    public static List<String> DEFAULT_BDUBS_VARIANT_MESSAGES = List.of(
            "I'm gonna rip Tango's face off for stealing my horse!",
            "Little fella? I'm 5'10\" (1.78m)!! That's average height!!!",
            "Its time for Redstone With Bdubs!",
            "HEY!!",
            "NOOooOOO!!!",
            "You'll speak when spoken to!",
            "Yes I know its upside down!!",
            "Blast!",
            "Very freaking funny!",
            "Sue today!",
            "You think you can win a free horse? Good luck sucker!",
            "Yes I got a step stool...",
            "You took my foot stool!?",
            "Roses are red, orchids are blue... I'm 5'10\" Tango, how about you?",
            "I don't want the history of it I just want the name!!! WHAt'S IT CALLED!!?",
            "The next arena awaits you unless you cannot, wait - you... Good luck trying to fight me!",
            "was killed by [Intentional Game Design] \"whoopsie!\"",
            "Did you know that horses have fast reflexes?",
            "Did you know that horses have excellent hearing?",
            "Did you know that horses have great memory?",
            "Did you know that horses have almost a 360 degree field of vision?",
            "Did you know that horses can sleep standing up?",
            "WHAOOOOOOOOAHYUHUHHHH",
            "WUAAAUHAAAAHHAAAHA",
            "Yeaah Bdubs I am the king of Decked Out today... WHAAA OPEN THE DOOR!! OPEN THE DOOR !!!",
            "If you're in an area for a long time, zombies are more likely to pick up items, but if its uh an area thats uh not very spawn uh... If not been area, er around much then it - can spawn.... If.....",
            "Follow me this way *walks face first into the door* ow",
            "We are friends, I don't know anything about remembering anything!",
            "Stop! Hammer time!",
            "I almost ate my diamond sword!",
            "This is something that looks like a diamond pickaxe, but is actually called a diamond sword.",
            "That's freaking it - I'm looking at an enderman.",
            "Wanna eat a cobblestone wall?",
            "Boom! ... nothing.",
            "Uh oh!",
            "Ideally, we figure out how many blocks Tango is away from me, multiply that by 1.618, and then that can give us the distance you need to be away from him."
    );

    public static List<String> TANGO_DECKED_OUT_MESSAGES = List.of(
            "Sneak",
            "Stability!",
            "Treasure Hunter!",
            "Moment of Clarity...",
            "Tread Lightly...",
            "Loot and Scoot!!",
            "Eerie Silence...",
            "Evasion!",
            "Stumble",
            "Stumble!",
            "Stumble!!",
            "STUMBLE!!!!",
            "Stumble Stumble Stumble!!!!",
            "STUMBLE -... STUMBLE!!",
            "Loot and Scoot! - Stumble! Stumble! Stumble! Stumble!"
    );

    public static List<Text> stringToText(List<String> stringList) {
        return stringList.stream().map(Text::of).toList();
    }

}
