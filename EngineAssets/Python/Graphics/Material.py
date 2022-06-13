class Material:
    def __init__(self):
        self.mainTex = "EngineAssets/Textures/Misc/blank.jpg"
        self.normalTex = "EngineAssets/Textures/Misc/blank.jpg"
        self.specularTex = "EngineAssets/Textures/Misc/blank.jpg"
        self.specularLighting = True
        self.useNormalMap = False
        self.useSpecularMap = False
        self.reflectivity = 1.0
        self.shineDamper = 10.0
        self.color = Vector3(255, 255, 255)

    def setMainTex(self, mainTex):
        self.mainTex = mainTex

    def setNormalTex(self, normalTex):
        self.normalTex = normalTex

    def setSpecularTex(self, specularTex):
        self.specularTex = specularTex

    def setSpecularLighting(self, specularLighting):
        self.specularLighting = specularLighting

    def setUseNormalMap(self, useNormalMap):
        self.useNormalMap = useNormalMap

    def setUseSpecularMap(self, useSpecularMap):
        self.useSpecularMap = useSpecularMap

    def setReflectivity(self, reflectivity):
        self.reflectivity = reflectivity

    def setShineDamper(self, shineDamper):
        self.shineDamper = shineDamper

    def setColor(self, color):
        self.color = color