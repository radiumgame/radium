class Light(Component):
    def __init__(self):
        super(Light, self).__init__()
        self.name = "Light"
        self.color = Color(255, 255, 255, 255)
        self.intensity = 1.0
        self.attenuation = 0.045
        self.lightType = "Point"

    def setColor(self, color):
        self.color = color
        SET_COMPONENT_ATTRIBUTE(self, "color", color)

    def setIntensity(self, intensity):
        self.intensity = intensity
        SET_COMPONENT_ATTRIBUTE(self, "intensity", intensity)

    def setAttenuation(self, attenuation):
        self.attenuation = attenuation
        SET_COMPONENT_ATTRIBUTE(self, "attenuation", attenuation)

    def setLightType(self, lightType):
        self.lightType = lightType
        SET_COMPONENT_ATTRIBUTE(self, "lightType", lightType.name)