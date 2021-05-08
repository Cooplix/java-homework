package com.binary_studio.fleet_commander.core.ship;

import java.util.Optional;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.AttackResult;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.ship.contract.CombatReadyVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class CombatReadyShip implements CombatReadyVessel {

	private String name;

	private PositiveInteger currentShieldHP;

	private PositiveInteger currentHullHP;

	private PositiveInteger currentCapacitorAmount;

	private PositiveInteger powerGridOutput;

	private PositiveInteger capacitorRechargeRate;

	private PositiveInteger speed;

	private PositiveInteger size;

	private final PositiveInteger shieldHPMax;

	private final PositiveInteger hullHPMax;

	private AttackSubsystem attackSubsystem;

	private PositiveInteger capacitorAmountMax;

	private DefenciveSubsystem defenciveSubsystem;

	public static CombatReadyShip construct(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size, AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {

		return new CombatReadyShip(name, shieldHP, hullHP, powergridOutput, capacitorAmount, capacitorRechargeRate,
				speed, size, attackSubsystem, defenciveSubsystem);
	}

	private CombatReadyShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powerGridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size, AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {
		this.name = name;
		this.shieldHPMax = shieldHP;
		this.currentShieldHP = shieldHP;
		this.hullHPMax = hullHP;
		this.currentHullHP = hullHP;
		this.powerGridOutput = powerGridOutput;
		this.capacitorAmountMax = capacitorAmount;
		this.currentCapacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.size = size;
		this.attackSubsystem = attackSubsystem;
		this.defenciveSubsystem = defenciveSubsystem;
	}

	@Override
	public void endTurn() {
		var rechargedCapacitor = this.capacitorRechargeRate.value() + this.currentCapacitorAmount.value();
		int checkedCapacitor = Math.min(rechargedCapacitor, this.capacitorAmountMax.value());
		this.currentCapacitorAmount = PositiveInteger.of(checkedCapacitor);

	}

	@Override
	public void startTurn() {
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public PositiveInteger getSize() {
		return this.size;
	}

	@Override
	public PositiveInteger getCurrentSpeed() {
		return this.speed;
	}

	@Override
	public Optional<AttackAction> attack(Attackable target) {
		Integer attack = this.attackSubsystem.getCapacitorConsumption().value();

		if (attack <= this.currentCapacitorAmount.value()) {
			this.currentCapacitorAmount = PositiveInteger.of(this.currentCapacitorAmount.value() - attack);
			PositiveInteger damage = this.attackSubsystem.attack(target);
			AttackAction attackAction = new AttackAction(damage, this, target, this.attackSubsystem);
			return Optional.of(attackAction);
		}
		else {
			return Optional.empty();
		}
	}

	@Override
	public AttackResult applyAttack(AttackAction attack) {
		AttackAction reducedAttack = this.defenciveSubsystem.reduceDamage(attack);
		PositiveInteger damage = reducedAttack.damage;

		var shieldHPRemaining = this.currentShieldHP.value() - damage.value();
		if (shieldHPRemaining >= 0) {
			this.currentShieldHP = PositiveInteger.of(shieldHPRemaining);
			return new AttackResult.DamageRecived(reducedAttack.weapon, damage, this);
		}

		PositiveInteger remainingDamageAmount = PositiveInteger.of(Math.abs(shieldHPRemaining));
		this.currentShieldHP = PositiveInteger.of(0);

		var hullHPRemaining = this.currentHullHP.value() - remainingDamageAmount.value();
		if (hullHPRemaining > 0) {
			this.currentHullHP = PositiveInteger.of(hullHPRemaining);
			return new AttackResult.DamageRecived(reducedAttack.weapon, damage, this);
		}

		return new AttackResult.Destroyed();
	}

	@Override
	public Optional<RegenerateAction> regenerate() {
		PositiveInteger defenseCharAmount = this.defenciveSubsystem.getCapacitorConsumption();

		if (defenseCharAmount.value() <= this.currentCapacitorAmount.value()) {
			this.currentCapacitorAmount = PositiveInteger
					.of(this.currentCapacitorAmount.value() - defenseCharAmount.value());
			RegenerateAction regenetareAction = this.defenciveSubsystem.regenerate();
			RegenerateAction hullRegenerated = regenerateHull(regenetareAction);
			RegenerateAction shieldRegenerated = regenerateShield(hullRegenerated);

			return Optional.of(shieldRegenerated);
		}
		else {
			return Optional.empty();
		}
	}

	private RegenerateAction regenerateShield(RegenerateAction regenetareAction) {
		var missingShieldHP = this.shieldHPMax.value() - this.currentShieldHP.value();
		PositiveInteger availableShieldRegenHP = regenetareAction.shieldHPRegenerated;

		int regeneShield = 0;

		if (missingShieldHP != 0) {
			regeneShield = (missingShieldHP > availableShieldRegenHP.value()) ? availableShieldRegenHP.value()
					: missingShieldHP;

			this.currentShieldHP = PositiveInteger.of(regeneShield);
		}
		return new RegenerateAction(regenetareAction.shieldHPRegenerated, PositiveInteger.of(regeneShield));
	}

	private RegenerateAction regenerateHull(RegenerateAction regenetareAction) {
		var missingHull = this.hullHPMax.value() - this.currentHullHP.value();

		PositiveInteger availableHullRegenerate = regenetareAction.hullHPRegenerated;

		int regenHull = 0;

		if (missingHull != 0) {
			regenHull = missingHull > availableHullRegenerate.value() ? availableHullRegenerate.value() : missingHull;
			this.currentHullHP = PositiveInteger.of(regenHull);
		}
		return new RegenerateAction(regenetareAction.shieldHPRegenerated, PositiveInteger.of(regenHull));
	}

}
