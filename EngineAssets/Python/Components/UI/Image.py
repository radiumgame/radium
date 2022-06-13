class Image(Component):
    def __init__(self):
        super(Image, self).__init__()
        self.position = Vector2(0, 0)
        self.size = Vector2(0, 0)
        self.texture = None
        self.color = Color(255, 255, 255, 255)
        self.layer = 0

    def setPosition(self, position):
        self.position = position
        SET_COMPONENT_ATTRIBUTE(self, "position", self.position)

    def setSize(self, size):
        self.size = size
        SET_COMPONENT_ATTRIBUTE(self, "size", self.size)

    def setTexture(self, texture):
        self.texture = texture
        SET_COMPONENT_ATTRIBUTE(self, "texture", self.texture)

    def setColor(self, color):
        self.color = color
        SET_COMPONENT_ATTRIBUTE(self, "color", self.color)

    def setLayer(self, layer):
        self.layer = layer
        SET_COMPONENT_ATTRIBUTE(self, "layerOrder", self.layer)