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
		if (this.currentCapacitorAmount.value() < this.defenciveSubsystem.getCapacitorConsumption().value()) {
			return Optional.empty();
		}
		else {
			PositiveInteger finalShieldRegeneration = PositiveInteger
					.of(this.currentShieldHP.value() + this.defenciveSubsystem.getMaxShieldRegeneration().value());
			PositiveInteger finalHullRegeneration = PositiveInteger
					.of(this.currentHullHP.value() + this.defenciveSubsystem.getMaxHullRegeneration().value());

			if (finalShieldRegeneration.value() > this.shieldHPMax.value()) {
				PositiveInteger shieldRegenerationDiff = PositiveInteger
						.of(this.shieldHPMax.value() - this.currentShieldHP.value());
				this.defenciveSubsystem.setShieldRegeneration(shieldRegenerationDiff);
			}
			else {
				this.defenciveSubsystem.setShieldRegeneration(this.defenciveSubsystem.getMaxShieldRegeneration());
			}
			if (finalHullRegeneration.value() > this.hullHPMax.value()) {
				PositiveInteger hullRegenerationDiff = PositiveInteger
						.of(this.hullHPMax.value() - this.currentHullHP.value());
				this.defenciveSubsystem.setHullRegeneration(hullRegenerationDiff);
			}
			else {
				this.defenciveSubsystem.setHullRegeneration(this.defenciveSubsystem.getMaxHullRegeneration());
			}
			RegenerateAction regenerateAction = this.defenciveSubsystem.regenerate();
			this.currentShieldHP = PositiveInteger
					.of(this.currentShieldHP.value() + regenerateAction.shieldHPRegenerated.value());
			this.currentHullHP = PositiveInteger
					.of(this.currentHullHP.value() + regenerateAction.hullHPRegenerated.value());
			this.currentCapacitorAmount = PositiveInteger.of(
					this.currentCapacitorAmount.value() - this.defenciveSubsystem.getCapacitorConsumption().value());
			return Optional.of(regenerateAction);
		}

	}

}
