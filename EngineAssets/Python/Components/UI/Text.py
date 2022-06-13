class Text(Component):
    def __init__(self):
        super(Text, self).__init__()
        self.position = Vector2(0, 0)
        self.fontSize = 64
        self.fontBlur = 0.0
        self.textAlign = "Left"
        self.text = ""
        self.color = Color(255, 255, 255, 255)
        self.layer = 0

    def setPosition(self, position):
        self.position = position
        SET_COMPONENT_ATTRIBUTE(self, "position", position)

    def setText(self, text):
        self.text = text
        SET_COMPONENT_ATTRIBUTE(self, "text", text)

    def setFontSize(self, fontSize):
        self.fontSize = fontSize
        SET_COMPONENT_ATTRIBUTE(self, "fontSize", fontSize)

    def setFontBlur(self, fontBlur):
        self.fontBlur = fontBlur
        SET_COMPONENT_ATTRIBUTE(self, "fontBlur", fontBlur)

    def setTextAlign(self, textAlign):
        self.textAlign = textAlign
        SET_COMPONENT_ATTRIBUTE(self, "textAlign", textAlign)

    def setColor(self, color):
        self.color = color
        SET_COMPONENT_ATTRIBUTE(self, "color", color)

    def setLayer(self, layer):
        self.layer = layer
        SET_COMPONENT_ATTRIBUTE(self, "layerOrder", layer)

    