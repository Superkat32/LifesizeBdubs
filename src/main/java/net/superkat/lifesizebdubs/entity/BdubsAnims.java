package net.superkat.lifesizebdubs.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.ClientUtil;

import java.util.List;

public class BdubsAnims {

    public static final String controller = "default";
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.idle");
    public static final RawAnimation SUGAR_IDLE_ANIM = RawAnimation.begin().thenLoop("animation.bdubs.sugaridle");

    public static final RawAnimation WAVE_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.wave");
    public static final RawAnimation CHEER_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.cheer");
    public static final RawAnimation GIVEN_SUGAR_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.givensugarohno");
    public static final RawAnimation LAUGH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.laugh");
    public static final RawAnimation NOD_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.nod");
    public static final RawAnimation BOW_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.yourewelcome");
    public static final RawAnimation TADA_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.tada");
    public static final RawAnimation SMOOTH_DANCE_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.smooth");
    public static List<RawAnimation> IDLE_ANIMS = List.of(WAVE_ANIM, CHEER_ANIM, GIVEN_SUGAR_ANIM, LAUGH_ANIM, NOD_ANIM, BOW_ANIM, TADA_ANIM, SMOOTH_DANCE_ANIM);

    public static final RawAnimation DESPAWN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.bdubs.leave");
    public static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlay("animation.bdubs.perish");

    public static void playIdleAnim(BdubsEntity bdubs) {
        int idleIndex = bdubs.getRandom().nextInt(IDLE_ANIMS.size());
        RawAnimation idleAnim = IDLE_ANIMS.get(idleIndex);
        triggerAnim(bdubs, idleAnim);
    }

    public static void triggerAnim(GeoEntity bdubs, RawAnimation anim) {
        bdubs.triggerAnim(controller, animString(anim));
    }

    public static void registerIdleAnims(AnimationController<BdubsEntity> controller) {
        for (RawAnimation anim : IDLE_ANIMS) {
            triggerableAnim(controller, anim);
            if(anim == LAUGH_ANIM) { //laughing sound register
                controller.setSoundKeyframeHandler(event -> {
                    PlayerEntity player = ClientUtil.getClientPlayer();
                    if(player != null) player.playSound(SoundEvents.ENTITY_VEX_AMBIENT, 0.5f, 1f);
                });
            }
        }
    }

    public static void triggerableAnim(AnimationController<BdubsEntity> controller, RawAnimation animation) {
        controller.triggerableAnim(animString(animation), animation);
    }

    public static String animString(RawAnimation anim) {
        return anim.getAnimationStages().getFirst().animationName();
    }

}
