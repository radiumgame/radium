class Rigidbody(Component):
    def __init__(self):
        super(Rigidbody, self).__init__()
        self.name = "Rigidbody"
        self.mass = 1
        self.drag = 0.1
        self.angularDrag = 0.1
        self.applyGravity = True
        self.isKinematic = False
        self.isStatic = False
        self.colliderRadius = 0.5
        self.colliderSize = Vector3(1, 1, 1)

    def setMass(self, mass):
        self.mass = mass
        SET_COMPONENT_ATTRIBUTE(self, "mass", mass)

    def setDrag(self, drag):
        self.drag = drag
        SET_COMPONENT_ATTRIBUTE(self, "drag", drag)

    def setAngularDrag(self, angularDrag):
        self.angularDrag = angularDrag
        SET_COMPONENT_ATTRIBUTE(self, "angularDrag", angularDrag)

    def setApplyGravity(self, applyGravity):
        self.applyGravity = applyGravity
        SET_COMPONENT_ATTRIBUTE(self, "applyGravity", applyGravity)

    def setKinematic(self, isKinematic):
        self.isKinematic = isKinematic
        SET_COMPONENT_ATTRIBUTE(self, "isKinematic", isKinematic)

    def setStatic(self, isStatic):
        self.isStatic = isStatic
        SET_COMPONENT_ATTRIBUTE(self, "isStatic", isStatic)

    def setColliderRadius(self, colliderRadius):
        self.colliderRadius = colliderRadius
        SET_COMPONENT_ATTRIBUTE(self, "radius", colliderRadius)

    def setColliderSize(self, colliderSize):
        self.colliderSize = colliderSize
        SET_COMPONENT_ATTRIBUTE(self, "colliderScale", colliderSize)