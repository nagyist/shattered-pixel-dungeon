/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseAbility;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class KingsCrown extends Item {
	
	private static final String AC_WEAR = "WEAR";
	
	{
		image = ItemSpriteSheet.CROWN;
		
		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_WEAR );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_WEAR)) {

			curUser = hero;
			if (hero.belongings.armor != null){
				GameScene.show( new WndChooseAbility(this, hero.belongings.armor, hero));
			} else {
				GLog.w( Messages.get(this, "naked"));
			}
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void upgradeArmor(Hero hero, Armor armor, ArmorAbility ability) {

		detach( hero.belongings.backpack );

		hero.sprite.centerEmitter().start( Speck.factory( Speck.KIT ), 0.05f, 10 );
		//TODO add a spell icon?
		hero.spend( Actor.TICK );
		hero.busy();
		
		GLog.p( Messages.get(this, "upgraded"));
		
		ClassArmor classArmor = ClassArmor.upgrade( hero, armor );
		if (hero.belongings.armor == armor) {
			
			curUser.belongings.armor = classArmor;
			((HeroSprite)curUser.sprite).updateArmor();
			classArmor.activate(curUser);
			
		} else {
			
			armor.detach( curUser.belongings.backpack );
			classArmor.collect( curUser.belongings.backpack );
			
		}
		hero.armorAbility = ability;
		Talent.initArmorTalents(hero);
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );
	}

}
