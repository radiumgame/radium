package Radium;

import Radium.Components.Graphics.MeshFilter;
import Radium.Components.Graphics.MeshRenderer;
import Radium.Objects.GameObject;
import RadiumEditor.Console;
import Radium.Graphics.Mesh;
import Radium.Graphics.Vertex;
import Radium.Math.Vector.Vector2;
import Radium.Math.Vector.Vector3;
import org.lwjgl.assimp.*;

import java.io.File;

public class ModelLoader {

    protected ModelLoader() {}

    public static GameObject LoadModel(String filepath) {
        return LoadModel(filepath, true);
    }

    public static GameObject LoadModel(String filePath, boolean instantiate) {
        AIScene scene = Assimp.aiImportFile(filePath, Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_CalcTangentSpace);

        if (scene == null) {
            Console.Log("Couldn't load model at " + filePath + " | Check if there are muliple meshes in the object. Make sure there is only one mesh in the object.");
            return null;
        }

        GameObject parent = new GameObject(true);
        parent.name = new File(filePath).getName();
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            int vertexCount = mesh.mNumVertices();

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

                AIVector3D tangent = tangents.get(v);
                AIVector3D bitangent = bitangents.get(v);

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
            gameObject.name = mesh.mName().dataString();
            gameObject.SetParent(parent);
        }

        return parent;
    }

}
