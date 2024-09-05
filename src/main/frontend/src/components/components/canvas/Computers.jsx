import {Suspense, useEffect, useState} from 'react'
import { Canvas } from '@react-three/fiber'
import { OrbitControls, Preload, useGLTF } from '@react-three/drei'
import CanvasLoader from '../Loader.jsx';
import { Mesh, MeshBasicMaterial  } from '@react-three/fiber';

const Computers = () => {
  const computer = useGLTF('/public/desktop_pc/scene.gltf')

  return (
    <mesh>
  <hemisphereLight intensity={0.15} groundColor="black" />
  <pointLight intensity={1} />
  <Mesh
    geometry={computer.scene.children[0].geometry}
    material={new MeshBasicMaterial({ color: 'white' })}
    scale={0.75}
    position={[0, -3.25, -1.5]}
    rotation={[-0.1, -0.2, -0.1]}
  />
</mesh>
  )
}

const ComputersCanvas = () =>{
  return(
    <Canvas
      frameloop='demand'
      shadows
      camera={{position: [20,3,5], fov:25 } }
      gl={{preserveDrawingBuffer: true}}
    >
      <Suspense fallback={<CanvasLoader />}>
        <OrbitControls 
          enableZoom={false}
          maxPolarAngle={Math.PI / 2}
          minPolarAngle={Math.PI / 2}  
        />
        <Computers />
      </Suspense>

      <Preload all />
    </Canvas>
  )
}

export default Computers