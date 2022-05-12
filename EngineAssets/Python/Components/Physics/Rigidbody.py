class Rigidbody(Component):
    def __init__(self):
        super(Rigidbody, self).__init__()
        self.name = "Rigidbody"
        self.mass = 1

    def setMass(self, mass):
        self.mass = mass
        SET_COMPONENT_ATTRIBUTE(self, "mass", mass)