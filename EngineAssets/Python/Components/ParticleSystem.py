class ParticleSystem(Component):
    def __init__(self):
        super(ParticleSystem, self).__init__()
        self.name = "Particle System"

        self.particleOffset = Vector3(0, 0, 0)
        self.randomOffset = False
        self.offsetRange1 = Vector3(0, 0, 0)
        self.offsetRange2 = Vector3(1, 1, 1)

        self.particleRotation = 0.0
        self.randomRotation = False
        self.rotationRange = Vector2(0, 360)

        self.particleSize = Vector2(0.25, 0.25)
        self.randomSize = False
        self.sizeRange = Vector2(0.25, 0.5)

        self.lifetime = 5.0
        self.randomLifetime = False
        self.lifetimeRange = Vector2(5.0, 10.0)

        self.emissionRate = 10.0
        self.particleSpeed = Vector3(1, 1, 1)
        self.randomSpeed = False
        self.speedRange1 = Vector3(0.5, 0.5, 0.5)
        self.speedRange2 = Vector3(1, 1, 1)

        self.blendType = "Alpha"
        self.transparent = False
        self.atlasSize = Vector2(1, 1)

        self.color = Color(1, 1, 1, 1)
        self.maxParticles = 1000

    def setParticleOffset(self, offset):
        self.particleOffset = offset
        SET_COMPONENT_ATTRIBUTE(self, "positionOffset", self.particleOffset)

    def setRandomOffset(self, randomOffset):
        self.randomOffset = randomOffset
        SET_COMPONENT_ATTRIBUTE(self, "randomPositionOffset", self.randomOffset)

    def setOffsetRange(self, offset1, offset2):
        self.offsetRange1 = offset1
        self.offsetRange2 = offset2
        SET_COMPONENT_ATTRIBUTE(self, "positionOffsetLimits1", self.offsetRange1)
        SET_COMPONENT_ATTRIBUTE(self, "positionOffsetLimits2", self.offsetRange2)

    def setParticleRotation(self, rotation):
        self.particleRotation = rotation
        SET_COMPONENT_ATTRIBUTE(self, "particleRotation", self.particleRotation)

    def setRandomRotation(self, randomRotation):
        self.randomRotation = randomRotation
        SET_COMPONENT_ATTRIBUTE(self, "randomRotation", self.randomRotation)

    def setRotationRange(self, rotationRange):
        self.rotationRange = rotationRange
        SET_COMPONENT_ATTRIBUTE(self, "rotationLimits", self.rotationRange)

    def setParticleSize(self, size):
        self.particleSize = size
        SET_COMPONENT_ATTRIBUTE(self, "particleSize", self.particleSize)

    def setRandomSize(self, randomSize):
        self.randomSize = randomSize
        SET_COMPONENT_ATTRIBUTE(self, "randomSize", self.randomSize)

    def setSizeRange(self, sizeRange):
        self.sizeRange = sizeRange
        SET_COMPONENT_ATTRIBUTE(self, "particleSizeLimits", self.sizeRange)

    def setLifetime(self, lifetime):
        self.lifetime = lifetime
        SET_COMPONENT_ATTRIBUTE(self, "particleLifetime", self.lifetime)

    def setRandomLifetime(self, randomLifetime):
        self.randomLifetime = randomLifetime
        SET_COMPONENT_ATTRIBUTE(self, "randomLifetime", self.randomLifetime)

    def setLifetimeRange(self, lifetimeRange):
        self.lifetimeRange = lifetimeRange
        SET_COMPONENT_ATTRIBUTE(self, "lifetimeLimits", self.lifetimeRange)

    def setEmissionRate(self, emissionRate):
        self.emissionRate = emissionRate
        SET_COMPONENT_ATTRIBUTE(self, "emissionRate", self.emissionRate)

    def setParticleSpeed(self, speed):
        self.particleSpeed = speed
        SET_COMPONENT_ATTRIBUTE(self, "particleSpeed", self.particleSpeed)

    def setRandomSpeed(self, randomSpeed):
        self.randomSpeed = randomSpeed
        SET_COMPONENT_ATTRIBUTE(self, "randomSpeed", self.randomSpeed)

    def setSpeedRange(self, speedRange1, speedRange2):
        self.speedRange1 = speedRange1
        self.speedRange2 = speedRange2
        SET_COMPONENT_ATTRIBUTE(self, "speedLimits1", self.speedRange1)
        SET_COMPONENT_ATTRIBUTE(self, "speedLimits2", self.speedRange2)

    def setBlendType(self, blendType):
        self.blendType = blendType
        SET_COMPONENT_ATTRIBUTE(self, "blendType", self.blendType)

    def setTransparent(self, transparent):
        self.transparent = transparent
        SET_COMPONENT_ATTRIBUTE(self, "transparent", self.transparent)

    def setAtlasSize(self, atlasSize):
        self.atlasSize = atlasSize
        SET_COMPONENT_ATTRIBUTE(self, "atlasSize", self.atlasSize)

    def setColor(self, color):
        self.color = color
        SET_COMPONENT_ATTRIBUTE(self, "color", self.color)

    def setMaxParticles(self, maxParticles):
        self.maxParticles = maxParticles
        SET_COMPONENT_ATTRIBUTE(self, "maxParticles", self.maxParticles)