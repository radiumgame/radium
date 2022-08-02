class Mesh:
    def __init__(self):
        self.vertices = []
        self.uvs = []
        self.normals = []
        self.indices = []

    def addVertex(self, vertex):
        self.vertices.append(vertex)

    def addUV(self, uv):
        self.uvs.append(uv)

    def addNormal(self, normal):
        self.normals.append(normal)

    def addIndex(self, index):
        self.indices.append(index)