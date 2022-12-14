class GameObject:
    def __init__(self):
        self.transform = Transform(Vector3.zero(), Vector3.zero(), Vector3.one())
        self.components = []
        self.name = "New Game Object"
        self.id = "EMPTY_ID"
        self.parent = None
        self.group = "Default Layer"
        self.active = True
        self.destroyed = False

    def create(self):
        instantiate(self)
        return self

    def destroy(self):
        self.transform = None
        self.components = None
        self.name = None
        self.destroyed = True
        UPDATE_GAMEOBJECT(self)

    def setActive(self, active):
        self.active = active
        UPDATE_GAMEOBJECT(self)

    def addComponent(self, component):
        component.gameObject = self
        self.components.append(component)

        SET_COMPONENT_ATTRIBUTE(component, "NEW_COMPONENT", component.name)

        return component
        
    def getComponent(self, componentName):
        for component in self.components:
            if component.name == componentName:
                return component

        return GET_ENGINE_COMPONENT(componentName)

    def getScript(self, scriptName):
        return GET_SCRIPT(self.id, scriptName)

    def removeComponent(self, component):
        if self.components.__contains__(component):
            self.components.remove(component)

        SET_COMPONENT_ATTRIBUTE(component, "REMOVE_COMPONENT", component.name)

    def setGroup(self, group):
        self.group = group
        UPDATE_GAMEOBJECT(self)

    def setName(self, name):
        self.name = name
        UPDATE_GAMEOBJECT(self)

    def setPosition(self, position):
        self.transform.position = position
        UPDATE_GAMEOBJECT(self)

    def setRotation(self, rotation):
        self.transform.rotation = rotation
        UPDATE_GAMEOBJECT(self)

    def setScale(self, scale):
        self.transform.scale = scale
        UPDATE_GAMEOBJECT(self)

    def translate(self, translation):
        self.transform.position += translation
        UPDATE_GAMEOBJECT(self)

    def rotate(self, rotation):
        self.transform.rotation += rotation
        UPDATE_GAMEOBJECT(self)

    def scale(self, dilation):
        self.transform.scale *= dilation
        UPDATE_GAMEOBJECT(self)

    def updateAttributes(self):
        UPDATE_GAMEOBJECT(self)

    def setParent(self, parent):
        self.parent = parent
        UPDATE_GAMEOBJECT(self)

    def exists(self):
        return GameObject.find(self.name) != None

    @classmethod
    def find(cls, name):
        objects = GET_ENGINE_OBJECTS()
        for obj in objects:
            if obj.name == name:
                return obj

        return None
