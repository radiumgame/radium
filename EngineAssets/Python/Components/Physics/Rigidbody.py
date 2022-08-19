class Rigidbody(Component):
    def __init__(self):
        super(Rigidbody, self).__init__()
        self.name = "Rigidbody"
        self.mass = 1
        self.drag = 0.1
        self.angularDrag = 0.1
        self.applyGravity = True
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

    def setColliderRadius(self, colliderRadius):
        self.colliderRadius = colliderRadius
        SET_COMPONENT_ATTRIBUTE(self, "radius", colliderRadius)

    def setColliderSize(self, colliderSize):
        self.colliderSize = colliderSize
        SET_COMPONENT_ATTRIBUTE(self, "colliderScale", colliderSize)

    def addForce(self, force):
        CALL_COMPONENT_METHOD(self, "AddForce", [float(force.x), float(force.y), float(force.z)])

    def addTorque(self, force):
        CALL_COMPONENT_METHOD(self, "AddTorque", [float(force.x), float(force.y), float(force.z)])

    def setVelocity(self, velocity):
        CALL_COMPONENT_METHOD(self, "SetVelocity", [float(velocity.x), float(velocity.y), float(velocity.z)])

    def setAngularVelocity(self, velocity):
        CALL_COMPONENT_METHOD(self, "SetAngularVelocity", [float(velocity.x), float(velocity.y), float(velocity.z)])