class MeshRenderer(Component):
    def __init__(self):
        super(MeshRenderer, self).__init__()
        self.name = "Mesh Renderer"

    def changeRenderer(self, renderType):
        SET_COMPONENT_ATTRIBUTE(self, "renderType", renderType)