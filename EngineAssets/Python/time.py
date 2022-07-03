class Time:
    def __init__(self):
        raise Exception("Time class is not meant to be instantiated")

    @classmethod
    def deltaTime(cls):
        return GET_TIME_PROPERTY("deltaTime")