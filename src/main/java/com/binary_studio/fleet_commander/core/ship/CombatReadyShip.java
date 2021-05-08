package com.binary_studio.fleet_commander.core.ship;

import java.util.Optional;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.AttackResult;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.ship.contract.CombatReadyVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;

public final class CombatReadyShip implements CombatReadyVessel {

	private String name;

	private PositiveInteger shieldHP;

	private PositiveInteger hullHP;

	private PositiveInteger capacitor;

	private PositiveInteger capacitorRegeneration;

	private PositiveInteger pg;

	private PositiveInteger currentSpeed;

	private PositiveInteger size;

	private AttackSubsystem attackSubsystem;

	public CombatReadyShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP, PositiveInteger capacitor,
			PositiveInteger capacitorRegeneration, PositiveInteger pg, PositiveInteger currentSpeed,
			PositiveInteger size) {
		this.name = name;
		this.shieldHP = shieldHP;
		this.hullHP = hullHP;
		this.capacitor = capacitor;
		this.capacitorRegeneration = capacitorRegeneration;
		this.pg = pg;
		this.currentSpeed = currentSpeed;
		this.size = size;
	}

	@Override
	public void endTurn() {
		// TODO: Ваш код здесь :)

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
		return this.currentSpeed;
	}

	@Override
	public Optional<AttackAction> attack(Attackable target) {
		if (capacitor.value() < attackSubsystem.getCapacitorConsumption().value()) {
			return Optional.empty();
		}


		return null;
	}

	@Override
	public AttackResult applyAttack(AttackAction attack) {
		// TODO: Ваш код здесь :)
		return null;
	}

	@Override
	public Optional<RegenerateAction> regenerate() {
		// TODO: Ваш код здесь :)
		return null;
	}

}
