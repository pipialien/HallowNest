package Hallownest.RazIntent;

import Hallownest.HallownestMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static Hallownest.HallownestMod.makeUIPath;

public class DeathIntent extends CustomIntent {

    public static final String ID = HallownestMod.makeID("DeathIntent");

    private static final UIStrings uiStrings;
    private static final String[] TEXT;


    public DeathIntent() {
        super(IntentEnums.DEATH, TEXT[0],
                makeUIPath("deathIntent_L.png"),
                makeUIPath("deathIntent.png"));
    }

    @Override
    public String description(AbstractMonster mo) {
        return TEXT[1];
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        TEXT = uiStrings.TEXT;
    }
}