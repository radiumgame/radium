package Radium;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Math.QuaternionUtility;
import Radium.Objects.GameObject;
import Radium.Util.FileUtility;
import RadiumEditor.Console;
import Radium.Graphics.Mesh;
import Radium.Graphics.Vertex;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import org.joml.Quaternionf;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Loads models from files such as FBX, OBJ, and DAE
 */
public class ModelLoader {

    protected ModelLoader() {}

    /**
     * Loads a model and adds it to the scene
     * @param filepath Model filepath
     * @return GameObject constructed from model
     */
    public static GameObject LoadModel(String filepath) {
        return LoadModel(filepath, true);
    }

    /**
     * Loads a model and adds it to the scene if instantiate is true
     * @param filePath Model filepath
     * @param instantiate Add it to the scene
     * @return GameObject constructed from model
     */
    public static GameObject LoadModel(String filePath, boolean instantiate) {
        AIScene scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace);

        if (scene == null) {
            Console.Log("Couldn't load model at " + filePath + " | Check if there are muliple meshes in the object. Make sure there is only one mesh in the object.");
            return null;
        }

        GameObject parent = new GameObject(true);
        parent.name = new File(filePath).getName().replace("." + FileUtility.GetFileExtension(new File(filePath)), "");
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            int vertexCount = mesh.mNumVertices();

            AINode node = null;
            boolean nodeFound = false;
            Vector3 nodePosition = null, nodeRotation = null, nodeScale = null;
            try {
                node = AINode.create(scene.mRootNode().mChildren().get(i));
                AIVector3D position = AIVector3D.create(), scale = AIVector3D.create();
                AIQuaternion rotation = AIQuaternion.create();
                Assimp.aiDecomposeMatrix(node.mTransformation(), scale, rotation, position);
                nodePosition = new Vector3(position.x(), position.y(), position.z());
                nodeScale = new Vector3(scale.x() / 100, scale.y() / 100, scale.z() / 100);
                Quaternionf quatf = new Quaternionf(rotation.x(), rotation.y(), rotation.z(), rotation.w());
                nodeRotation = QuaternionUtility.GetEuler(quatf);

                nodeFound = true;
            } catch (Exception exception) {

            }

            AIVector3D.Buffer vertices = mesh.mVertices();
            AIVector3D.Buffer normals = mesh.mNormals();
            AIVector3D.Buffer tangents = mesh.mTangents();
            AIVector3D.Buffer bitangents = mesh.mBitangents();

            Vertex[] vertexList = new Vertex[vertexCount];

            for (int v = 0; v < vertexCount; v++) {
                AIVector3D vertex = vertices.get(v);
                Vector3 meshVertex = new Vector3(vertex.x(), vertex.y(), vertex.z());

                AIVector3D normal = normals.get(v);
                Vector3 meshNormal = new Vector3(normal.x(), normal.y(), normal.z());

                AIVector3D tangent = AIVector3D.create().set(0, 0, 0);
                AIVector3D bitangent = AIVector3D.create().set(0, 0, 0);
                if (tangents != null) {
                    tangent = tangents.get(v);
                }
                if (bitangents != null) {
                    bitangent = bitangents.get(v);
                }

                Vector2 meshTextureCoord = new Vector2(0, 0);
                if (mesh.mNumUVComponents().get(0) != 0) {
                    AIVector3D texture = mesh.mTextureCoords(0).get(v);
                    meshTextureCoord.x = texture.x();
                    meshTextureCoord.y = texture.y();
                }

                vertexList[v] = new Vertex(meshVertex, meshNormal, meshTextureCoord);
                vertexList[v].SetTangent(new Vector3(tangent.x(), tangent.y(), tangent.z()));
                vertexList[v].SetBitangent(new Vector3(bitangent.x(), bitangent.y(), bitangent.z()));
            }

            int faceCount = mesh.mNumFaces();
            AIFace.Buffer indices = mesh.mFaces();
            int[] indicesList = new int[faceCount * 3];

            for (int j = 0; j < faceCount; j++) {
                AIFace face = indices.get(j);
                indicesList[j * 3 + 0] = face.mIndices().get(0);
                indicesList[j * 3 + 1] = face.mIndices().get(1);
                indicesList[j * 3 + 2] = face.mIndices().get(2);
            }

            Mesh gameObjectMesh = new Mesh(vertexList, indicesList);
            GameObject gameObject = new GameObject(instantiate);
            gameObject.AddComponent(new MeshFilter(gameObjectMesh));
            gameObject.AddComponent(new MeshRenderer());

            if (nodeFound) {
                gameObject.name = node.mName().dataString();

                gameObject.transform.localPosition = nodePosition;
                gameObject.transform.localRotation = nodeRotation;
                gameObject.transform.localScale = nodeScale;
            }

            gameObject.SetParent(parent);
        }

        return parent;
    }

}
