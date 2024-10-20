import Search from './assets/assets/search.png';
import arrow from './assets/assets/backend.png';
import pencil from './assets/assets/pencil.png';

import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

const NewPost = () => {
    const [selectedImage, setSelectedImage] = useState(null);
    const [fileForUpload, setFileForUpload] = useState(null);
    const [Username, setUsername] = useState(localStorage.getItem("name"));
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        content: '',
        title: ''
    });

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const handleImageChange = (e) => {
        const file = e.target.files?.[0];
        setSelectedImage(file ? URL.createObjectURL(file) : undefined);
        setFileForUpload(file);
    };

    const uploadPost = async (e) => {
        console.log("upload Post Called!");
        e.preventDefault();
        if(!formData.content || !selectedImage) {
            setError("All fields and image are required");
            return;
        }
        const postFormData = new FormData();
        postFormData.append('post', JSON.stringify({
            title: formData.title,
            content: formData.content
        }))
        postFormData.append('resource', fileForUpload);
        try {
            setLoading(true);
            setError('');
            const response = await fetch('/api/auth/posts', {
                method: 'POST',
                body: postFormData
            })
            if (!response.ok) {
                throw new Error('Failed to upload post');
            }

            const result = await response.json();
            console.log("Upload success:", result);
            navigate("/posts")
        } catch (error) {
            console.log("Error: ", error);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="bg-[#0f0f0f] w-full h-screen flex ">
            <div className="bg-[#2C2C2C] w-1/4 h-screen fixed">
                <div className="absolute top-6">
                    <div className="mx-3">
                        <div className="flex justify-center">
                            <img src={localStorage.getItem("profilePic")} alt="pfp" className="w-1/3 h-1/3" />
                        </div>
                        <div className="flex justify-center my-3 font-semibold">
                            {Username}
                            <Link to="/Accounts">
                                <button className="w-5 h-5 bg-yellow-400 rounded-full hover:bg-yellow-600 mx-1 p-1">
                                    <img src={pencil} alt="pencil" />
                                </button>
                            </Link>
                        </div>
                    </div>

                    <div className="w-full screen my-24">
                        <div className="flex-col">
                            <div className="bg-gray-800 w-full h-14 ab my-2">
                                <Link to="/accounts">
                                    <button className="bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]">
                                        Account
                                    </button>
                                </Link>
                            </div>
                            <div className="bg-gray-800 w-full h-14 ab my-2">
                                <button className="bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]">
                                    FAQs
                                </button>
                            </div>
                            <div className="bg-gray-800 w-full h-14 ab my-2">
                                <button className="bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]">
                                    Help
                                </button>
                            </div>
                            <div className="bg-gray-800 w-full h-14 ab my-2">
                                <button className="bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-[#fa3838] hover:text-[#ffffff]">
                                    Sign-Out
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="absolute right-0 w-3/4 h-screen bg-[#5a5a5a] ">
                <div className="fixed right-0 w-3/4">
                    <nav className="bg-[#1D1D1D] h-12 rounded-lg m-3 flex items-center justify-start">
                        <div className="w-full h-full flex">
                            <div className="w-2/12 h-full flex items-center justify-center">
                                <img src={localStorage.getItem("profilePic")} alt="" className="w-12 h-12 m-1"/>
                            </div>
                            <div className="w-8/12 h-full flex items-center justify-center">
                                <input
                                    type="text"
                                    className="bg-[#D9D9D9] w-full h-9 rounded-2xl text-yellow-500 px-12 font-bold underline"
                                />
                                <button className="w-7 h-7 rounded-full fixed left-[585px]">
                                    <img src={Search} alt="search" className="w-7 h-7"/>
                                </button>
                            </div>
                            <div className="w-2/12 h-full flex items-center justify-center">
                                <Link to="/posts">
                                    <button
                                        className="bg-black rounded-full w-10 h-10 mx-5 m-1 flex justify-center align-middle p-1.5">
                                        <img src={arrow} alt="add" className="w-fit h-full rotate-180"/>
                                    </button>
                                </Link>
                            </div>
                        </div>
                    </nav>
                </div>

                <form onSubmit={uploadPost}>
                    <div className="flex flex-col w-full h-screen ">
                        <div className="w-full h-1/6 "></div>
                        <div className="w-full h-4/6 flex flex-col">
                            <div
                                className="w-full h-3/4 bg-[#1D1D1D] rounded-lg flex flex-col justify-center items-center">
                                <div className='w-1/2 h-1/6 flex justify-center items-center'>
                                    <input type="file" accept="image/*" name="uploadFile" onChange={handleImageChange}
                                           className='bg-black'/>
                                </div>
                                <div className="bg-[#4d4d4d] w-1/2 h-5/6 rounded-lg flex justify-center items-center">
                                    {selectedImage && (
                                        <img
                                            src={selectedImage}
                                            width={400}
                                            height={400}
                                            alt="Selected avatar"
                                            className="h-full w-fit object-contain"
                                        />
                                    )}
                                </div>
                            </div>
                            <div className="w-full h-1/4 flex justify-center items-center p-3">
                                <input name='content'
                                       type="text"
                                       placeholder="Content"
                                       className="w-2/3 h-3/4 bg-white text-black font-bold text-center"
                                       onChange={handleChange}
                                />
                            </div>
                        </div>
                        <div className="w-full h-1/6 flex justify-center items-center">
                            <button className="w-1/3 h-1/3 bg-black rounded-md" type='submit'>Post</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
);
};

export default NewPost;