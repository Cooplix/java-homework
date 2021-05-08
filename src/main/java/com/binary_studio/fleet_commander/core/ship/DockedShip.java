package com.binary_studio.fleet_commander.core.ship;

import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.exceptions.InsufficientPowergridException;
import com.binary_studio.fleet_commander.core.exceptions.NotAllSubsystemsFitted;
import com.binary_studio.fleet_commander.core.ship.contract.ModularVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class DockedShip implements ModularVessel {

	private String name;

	private PositiveInteger shieldHP;

	private PositiveInteger hullHP;

	private PositiveInteger powergridOutput;

	private PositiveInteger capacitorAmount;

	private PositiveInteger capacitorRechargeRate;

	private PositiveInteger speed;

	private PositiveInteger siz;

	private AttackSubsystem attackSubsystem;

	private DefenciveSubsystem defenciveSubsystem;

	public DockedShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP, PositiveInteger powergridOutput,
			PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate, PositiveInteger speed,
			PositiveInteger siz) {
		this.name = name;
		this.shieldHP = shieldHP;
		this.hullHP = hullHP;
		this.powergridOutput = powergridOutput;
		this.capacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.siz = siz;
	}

	public static DockedShip construct(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size) {

		return new DockedShip(name, shieldHP, hullHP, powergridOutput, capacitorAmount, capacitorRechargeRate, speed,
				size);
	}

	@Override
	public void fitAttackSubsystem(AttackSubsystem subsystem) throws InsufficientPowergridException {
		if (subsystem == null) {
			attackSubsystem = null;
		}
		else {
			if (defenciveSubsystem == null) {
				if (subsystem.getPowerGridConsumption().value() > powergridOutput.value()) {
					int missingPowerGrid = subsystem.getPowerGridConsumption().value() - powergridOutput.value();
					throw new InsufficientPowergridException(missingPowerGrid);
				}
			}
			else {
				if (subsystem.getPowerGridConsumption().value()
						+ defenciveSubsystem.getCapacitorConsumption().value() > powergridOutput.value()) {
					int missingPowerGrid = subsystem.getPowerGridConsumption().value()
							+ defenciveSubsystem.getPowerGridConsumption().value() - powergridOutput.value();
					throw new InsufficientPowergridException(missingPowerGrid);
				}
			}
			attackSubsystem = subsystem;
		}
	}

	@Override
	public void fitDefensiveSubsystem(DefenciveSubsystem subsystem) throws InsufficientPowergridException {
		if (subsystem == null) {
			defenciveSubsystem = null;
		}
		else {
			if (attackSubsystem == null) {
				if (subsystem.getPowerGridConsumption().value() > powergridOutput.value()) {
					int missingPowerGrid = subsystem.getPowerGridConsumption().value() - powergridOutput.value();
					throw new InsufficientPowergridException(missingPowerGrid);
				}
			}
			else {
				if (subsystem.getPowerGridConsumption().value()
						+ attackSubsystem.getCapacitorConsumption().value() > powergridOutput.value()) {
					int missingPowerGrid = subsystem.getPowerGridConsumption().value()
							+ attackSubsystem.getPowerGridConsumption().value() - powergridOutput.value();
					throw new InsufficientPowergridException(missingPowerGrid);
				}
			}
			defenciveSubsystem = subsystem;
		}

	}

	public CombatReadyShip undock() throws NotAllSubsystemsFitted {
		if (attackSubsystem == null) {
			throw NotAllSubsystemsFitted.attackMissing();
		}
		else if (defenciveSubsystem == null) {
			if (attackSubsystem == null) {
				throw NotAllSubsystemsFitted.bothMissing();
			}
			else {
				throw NotAllSubsystemsFitted.defenciveMissing();
			}
		}
		else {
			return new CombatReadyShip();
		}
	}

}
