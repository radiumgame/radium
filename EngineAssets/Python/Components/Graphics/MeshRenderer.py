class MeshRenderer(Component):
    def __init__(self):
        super(MeshRenderer, self).__init__()
        self.name = "Mesh Renderer"

        self.cullFaces = True
        self.transparent = False
        self.castShadows = True

    def changeRenderer(self, renderType):
        SET_COMPONENT_ATTRIBUTE(self, "renderType", renderType)

    def setCullFaces(self, cullFaces):
        self.cullFaces = cullFaces
        SET_COMPONENT_ATTRIBUTE(self, "cullFaces", cullFaces)

    def setTransparent(self, transparent):
        self.transparent = transparent
        SET_COMPONENT_ATTRIBUTE(self, "transparent", transparent)

    def setCastShadows(self, castShadows):
        self.castShadows = castShadows
        SET_COMPONENT_ATTRIBUTE(self, "castShadows", castShadows)