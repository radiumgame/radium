class MeshFilter(Component):
    def __init__(self):
        super(MeshFilter, self).__init__()

        self.name = "Mesh Filter"
        self.material = Material()
        self.mesh = None

    def updateMaterial(self):
        SET_COMPONENT_ATTRIBUTE(self, "material", self.material)

    def setMaterial(self, material):
        self.material = material
        SET_COMPONENT_ATTRIBUTE(self, "material", self.material)

    def setMesh(self, mesh):
        if isinstance(mesh, Mesh):
            self.mesh = mesh
            SET_COMPONENT_ATTRIBUTE(self, "mesh", mesh)
        else:
            SET_COMPONENT_ATTRIBUTE(self, "meshType", mesh)