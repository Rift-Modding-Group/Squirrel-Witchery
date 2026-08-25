package anightdazingzoroark.squirrelwitchery.client.renderer;

import anightdazingzoroark.riftlib.renderers.geo.GeoArmorRenderer;
import anightdazingzoroark.squirrelwitchery.client.model.WitchCostumeModel;
import anightdazingzoroark.squirrelwitchery.server.armor.WitchCostumeHolder;

public class WitchCostumeRenderer extends GeoArmorRenderer<WitchCostumeHolder> {
    public WitchCostumeRenderer() {
        super(new WitchCostumeModel(), WitchCostumeHolder::new);
        this.setHeadBone("hat");
        this.setBodyBone("robe");
        this.setRightArmBone("rightArm");
        this.setLeftArmBone("leftArm");
        this.setHipsBone("skirt");
        this.setRightBootBone("rightFoot");
        this.setLeftBootBone("leftFoot");
    }
}
