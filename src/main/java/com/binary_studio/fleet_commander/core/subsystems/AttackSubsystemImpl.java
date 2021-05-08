package com.binary_studio.fleet_commander.core.subsystems;

import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;

public final class AttackSubsystemImpl implements AttackSubsystem {

	private String name;

	private PositiveInteger powerGridRequirments;

	private PositiveInteger capacitorConsumption;

	private PositiveInteger optimalSpeed;

	private PositiveInteger optimalSize;

	private PositiveInteger baseDamage;

	private AttackSubsystemImpl(String name, PositiveInteger powerGridRequirments, PositiveInteger capacitorConsumption,
			PositiveInteger optimalSpeed, PositiveInteger optimalSize, PositiveInteger baseDamage) {
		this.name = name;
		this.powerGridRequirments = powerGridRequirments;
		this.capacitorConsumption = capacitorConsumption;
		this.optimalSpeed = optimalSpeed;
		this.optimalSize = optimalSize;
		this.baseDamage = baseDamage;

	}

	public static AttackSubsystemImpl construct(String name, PositiveInteger powergridRequirments,
			PositiveInteger capacitorConsumption, PositiveInteger optimalSpeed, PositiveInteger optimalSize,
			PositiveInteger baseDamage) throws IllegalArgumentException {
		if (name == null || "".equals(name.trim())) {
			throw new IllegalArgumentException("Name should be not null and not empty");
		}

		return new AttackSubsystemImpl(name, powergridRequirments, capacitorConsumption, optimalSpeed, optimalSize,
				baseDamage);
	}

	@Override
	public PositiveInteger getPowerGridConsumption() {
		return this.powerGridRequirments;
	}

	@Override
	public PositiveInteger getCapacitorConsumption() {
		return this.capacitorConsumption;
	}

	@Override
	public PositiveInteger attack(Attackable target) {
		double sizeReductionModifier = target.getSize().value() >= this.optimalSize.value() ? 1
				: (double) target.getSize().value() / this.optimalSize.value();
		double speedReductionModifier = target.getCurrentSpeed().value() <= this.optimalSpeed.value() ? 1
				: (double) this.optimalSpeed.value() / (2 * target.getCurrentSpeed().value());
		int damage = (int) Math
				.round(this.baseDamage.value() * Math.min(sizeReductionModifier, speedReductionModifier));
		return PositiveInteger.of(damage);
	}

	@Override
	public String getName() {
		return this.name;
	}

}
