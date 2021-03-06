package Hallownest.monsters.GreenpathEnemies;

import Hallownest.HallownestMod;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

public class BossFalseKnight extends AbstractMonster {

    public static final String ID = HallownestMod.makeID("BossFalseKnight");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);
    public static final String NAME = monsterStrings.NAME;
    public static final String[] MOVES = monsterStrings.MOVES;
    public static final String[] DIALOG = monsterStrings.DIALOG;
    // ****** MOVE AND STAT VALUES ********//
    private int bigGuard = 10;
    private int platedArmor = 10;
    private int rageGrowth = 5;
    private int baseRagehit = 6;
    private int rageLimit = 3;
    private int rageTimes = 0;
    private int rageGained = 0;
    private int maxHP = 250;
    private int minHP = 220;
    private int bigSmash = 15;
    private int bigsmashFrail = 2;
    private int fastSmashDmg = 7;
    private int fastSmashtimes = 2;
    private int numTurns = 0;
    // ******* END OF MOVE AND STAT VALUES *********//


    //LIST THE ANIM NAMES HERE IN COMMENTS FOR EASY MEMORY//
    private String animIdle = "Idle";
    private String animDef = "defend";
    private String animAtt = "bigsmash";
    private String animVuln = "weak";
    private String animSpec = "leapsmash";
    private String animBuff = "buff";

    // the Main "Constructer" iirc that's what the big method/function that the class relies upon is called.
    public BossFalseKnight()
    {
        //the stuff that gets sent up the line to AbstractMonster to do what it does
    /*here's what these refer to: (final String name, final String id, final int maxHealth, final float hb_x, final float hb_y, final float hb_w, final float hb_h, final String imgUrl,


	final float offsetX, final float offsetY,
	final boolean ignoreBlights: Not included as false by default?
	*/
        super(NAME, ID, 220, 0, 0, 400.0F, 400.0F, null, 1.0F, 1.0F);
        this.type = AbstractMonster.EnemyType.BOSS;

        if (AbstractDungeon.ascensionLevel >=9)
        {
            //For monsters encountered at higher ascension levels adds a bit more HP
            this.minHP += 10;
            this.maxHP += 10;

        }
        if (AbstractDungeon.ascensionLevel >=4)
        {

            this.bigSmash += 1;
            this.baseRagehit += 1;
            this.bigGuard += 5;
            //increases the power of his multihit and debufff for higher ascensions

        }
        if (AbstractDungeon.ascensionLevel >= 19)
        {
            this.rageLimit += 1;

        }
        //set the min and max hp bounds,
        setHp(this.minHP, this.maxHP);
        //****** DAMAGE INFO ARRAYS? **** //
        //creates a list 0,1,2 of damageinfos to pull from for later.
        this.damage.add(new DamageInfo(this, this.fastSmashDmg)); // attack 0 damage
        this.damage.add(new DamageInfo(this, this.bigSmash)); // attack 1 damage
        this.damage.add(new DamageInfo(this, this.baseRagehit)); //attack 2 damagee
        // **** END ARRAYS **** //
        loadAnimation(
                //loads the animation
                ("HallownestResources/images/monsters/Greenpath/falseknight/falseknight.atlas"),
                ("HallownestResources/images/monsters/Greenpath/falseknight/falseknight.json"), 0.9F);
        //starts the animation called idle i think, im unsire of the 1st variable, but i think the second is the animation name, and the 3rd is a boolean for islooping?
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        //no idea
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    @Override
    public void usePreBattleAction() {

        AbstractDungeon.scene.fadeOutAmbiance();
        AbstractDungeon.getCurrRoom().playBgmInstantly("FalseBGM");
        AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0], 4.0f, 4.0f));
    }

    //take turn is actually one of the later things to occur, it happens AFTER the action has been decided and displayed for a turn as intent. deciding the move happens in GetMove
    public void takeTurn()
    {

        //Just a handy little shortener Blank seems to use to make writing the actions easier
        AbstractPlayer p = AbstractDungeon.player;
        //very simple, it checks what you've assinged as .nextMove's value. that happens in getMove
        switch (this.nextMove)
        {
            case 0:  // calls attack 0 damage stored damage info, alternatively you could created the damage info inside the case too
                //FastAttack
                for (int i = 0; i < this.fastSmashtimes; ++i) {
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.2f));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(0), AbstractGameAction.AttackEffect.BLUNT_LIGHT, true));
                }
                break;
            case 1: //the Jumping attack + Frail action
                //
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "SPECIAL"));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(1), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, this, new FrailPower(p, this.bigsmashFrail, true), this.bigsmashFrail));
                break;
            case 2: //calls attack 2 damage info
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "DEFEND"));
                AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this, this, this.bigGuard));
                break;
            case 3: //rage
                AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "RAGE"));
                AbstractDungeon.actionManager.addToBottom(new WaitAction(0.3f));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                if (rageTimes == rageLimit - 1) {
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.4f));
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "ATTACK"));
                    AbstractDungeon.actionManager.addToBottom(new DamageAction(p, this.damage.get(2), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                }
                if (rageTimes < rageLimit - 1) {
                    AbstractDungeon.actionManager.addToBottom(new VFXAction(this, new InflameEffect(this), 0.25f));
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new StrengthPower(this, this.rageGrowth), this.rageGrowth));
                    rageGained += rageGrowth;
                }
                rageTimes++;
                if (rageTimes >= rageLimit) {
                    AbstractDungeon.actionManager.addToBottom(new WaitAction(0.4f));
                    AbstractDungeon.actionManager.addToBottom(new ChangeStateAction(this, "VULN"));
                }
                break;
            case 4: //VULN
                //add SFX later
                AbstractDungeon.actionManager.addToTop(new TalkAction(this, DIALOG[1], 4.0f, 4.0f));
                if (this.hasPower(StrengthPower.POWER_ID)) {
                    AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(this, this, StrengthPower.POWER_ID, rageGained));
                }
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new VulnerablePower(this, 1, true),1));
                break;
        }
        //unsure here, I think it basically  uses an action to send this monsters data to the AbstractMonster.rollMove , which activates the monsterFalseKnight.getMove and sends a rng amount?
        //this seems to basically be the "get the intent for the next turn's move thing"
        //unsure here, I think it basically  uses an action to send this monsters data to the AbstractMonster.rollMove , which activates the DefaultMonster.getMove and sends a rng amount?
        //this seems to basically be the "get the intent for the next turn's move thing"
        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    //this is the method that the ChangeStateAction requires within the class of any monster thant calls it.
    public void changeState(String key)
    {
        switch (key)
        {
            //for each key, it has a simple little transition between animations,
            //for this example, sets the animation to attack, and not looping, then adds the looping idle animation as next in line.
            case "ATTACK":
                this.state.setAnimation(0, animAtt, false);
                this.state.addAnimation(0, animIdle, true, 0.9F);
                break;
            case "DEFEND":
                this.state.setAnimation(0, animDef, false);
                this.state.addAnimation(0, animIdle, true, 0.0F);
                break;
            case "SPECIAL":
                this.state.setAnimation(0, animSpec, false);
                this.state.addAnimation(0, animIdle, true, 0.0F);
                break;
            case "RAGE":
                this.state.setAnimation(0, animAtt, false);
                this.state.addAnimation(0, animBuff, false,0.0F);
                this.state.addAnimation(0, animIdle, true, 0.0F);
                break;
            case "VULN":
                this.state.setAnimation(0, animVuln, true);
                break;
        }
    }
    //Unsure, but I think this handles the event of Taking damage, not sure if it's needed or not.
    //basically works just like the change state attack, the oof animation plays once. then it sets the looping idle animation to play again afterwards.
  /*  public void damage(DamageInfo info)
    {
        super.damage(info);
        //just checks to make sure the attack came from the plaer basically.
        if ((info.owner != null) && (info.type != DamageInfo.DamageType.THORNS) && (info.output > 0))
        {
            this.state.setAnimation(0, "oof", false);
            this.state.addAnimation(0, "Idle", true, 0.0F);
        }
    }
*/    //This is where the monster recieves a roll between 0 and 99 (so a full 1/100 chances is easily done) the getMove method uses that number to determine probability of assigning a specific action
    //
    protected void getMove(int i)
    {
        numTurns++;
        if (rageTimes == rageLimit){
            setMove(MOVES[4],(byte) 4, Intent.UNKNOWN);
            rageTimes = 0;
            return;
        }
        if (this.numTurns == 4){
            setMove(MOVES[3],(byte) 3, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(2)).base);

            return;
        }
        if (((i <=10) && (numTurns > 4)) || ((this.lastMove((byte)3)) && (rageTimes < rageLimit))) {

            if (rageTimes == rageLimit - 1) {
                setMove(MOVES[3],(byte) 3, Intent.ATTACK, ((DamageInfo) this.damage.get(2)).base, 2, true);
                return;
            }
            setMove(MOVES[3],(byte) 3, Intent.ATTACK_BUFF, ((DamageInfo) this.damage.get(2)).base);
            return;
        }
        // so for this, it's a modified probability. it's a 30% chance (any roll less than 30) but it's also gauranteed if it's the first turn of the combat
        if (i < 40) {
            setMove((byte) 1, Intent.ATTACK_DEBUFF, ((DamageInfo) this.damage.get(1)).base);
        } else if (i < 75) {
            setMove((byte)0, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base, fastSmashtimes, true);
            //so anything over 44 will be this i think?  so 65% generic attacks.
        } else if (i >= 75){
            setMove((byte)2, Intent.DEFEND);
        }
    }
    //this kills the dude, Shake is a good standard animation for dying, onbossvictory is important for score and a few otherthings.
    public void die() {
        this.state.setTimeScale(0.1f);
        this.useShakeAnimation(5.0f);
        super.die();
        this.onBossVictoryLogic();
    }

    //Assigns byte values to the attack names. I can't find this directly called, maybe it's just put in the output for debugging
    public static class MoveBytes
    {
        public static final byte FASTATTACK = 0;
        public static final byte BIGATTACK = 1;
        public static final byte BLOCK = 2;
        public static final byte RAMPAGE = 3;
        public static final byte VULN= 4;
    }



}
