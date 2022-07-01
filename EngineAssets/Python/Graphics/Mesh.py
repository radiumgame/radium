class Mesh:
    def __init__(self):
        self.vertices = []
        self.uvs = []
        self.normals = []
        self.tangents = []
        self.bitangents = []
        self.indices = []

    def addVertex(self, vertex):
        self.vertices.append(vertex)

    def addUV(self, uv):
        self.uvs.append(uv)

    def addNormal(self, normal):
        self.normals.append(normal)

    def addTangent(self, tangent):
        self.tangents.append(tangent)

    def addBitangent(self, bitangent):
        self.bitangents.append(bitangent)

    def addIndex(self, index):
        self.indices.append(index)