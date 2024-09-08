import {useNavigate} from 'react-router-dom';
import {useState} from 'react';

const SignUp = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        username: '',
        name: '',
        password: '',
        profile: 'P1'
    });

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };


    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch('/api/auth/signup', {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json(); // Parsing JSON response
            if (response.ok != true) {
                console.log('Error: ', result.message);
            } else {
                navigate(`/`);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (

        <section className="relative bg-white w-full h-screen ">

            <div className="flex justify-evenly ">
                <div className="flex bg-yellow-400 h-screen w-1/3 justify-center align-middle">
                    <div className="bg-red w-96 relative ">
                        <div><br/><br/><br/><br/></div>
                        <div>
                            <div className=" text-5xl flex align-middle justify-center p-8">
                                <h3 className="font-semibold text-black underline">Sign up</h3>
                            </div>
                            <div className="flex justify-center ">
                                <form onSubmit={handleSubmit}>
                                    <input type="email" name='email' onChange={handleChange} placeholder="Email"
                                           className=" p-2 m-8 h-8 w-72 flex rounded bg-white text-black placeholder-black"/>
                                    <input type="name" name='name' onChange={handleChange} placeholder="Name"
                                           className=" p-2 m-8 h-8 w-72 flex rounded bg-white text-black placeholder-black"/>
                                    <input type="username" name='username' onChange={handleChange}
                                           placeholder="Username"
                                           className=" p-2 m-8 h-8 w-72 flex rounded bg-white text-black placeholder-black"/>
                                    <input type="password" name='password' onChange={handleChange}
                                           placeholder="Password"
                                           className=" p-2 m-8 h-8 w-72 flex rounded text-black bg-white placeholder-black"/>
                                    <input type="password" name='same-password' placeholder="Confirm Password"
                                           className=" p-2 m-8 h-8 w-72 flex rounded text-black bg-white placeholder-black"/>
                                    <button className="flex justify-center align-middle w-72 h-10 p-2 m-8 rounded-lg bg-[#1E1E1E] 
                                    hover:bg-[#564a02] font-medium" onClick={"#"}>Sign in
                                    </button>
                                </form>

                            </div>


                        </div>

                    </div>

                </div>
                <div className="relative w-2/3 bg-[#1E1E1E] h-screen ">
                    {/* <div className="hexagon2 fixed right-96" />
                    <div className="hexagon2 fixed right-96 top-[343px]" />
                    <div className="hexagon2 fixed right-[490px] top-[170px]" />
                    <div className="hexagon2 fixed right-[490px] top-[170px]" />
                    <div className="hexagon2 fixed right-[490px] top-[515px]" />
                    <div className="hexagon2 fixed right-[605px] top-[343px]" /> */}
                    <div className="font-semibold">
                        <br/><br/><br/>
                        <h1 className="text-8xl p-5 m-5 mx-24">Create an <br/><span
                            className="text-yellow-400 font-extrabold">Account!</span></h1>
                    </div>
                    <div>

                        <div className="p-10 w-2/3 mx-20">
                            <p>

                                -Create a BugBee account and connect with your collegues from your university <br/>
                                -Create a Hive with community among people with shared interests or backgrounds. <br/>
                                -Use this platform to share news, memes, documents, and various other stuffs with your
                                friends
                            </p>
                        </div>

                    </div>


                </div>


            </div>

        </section>

    )
}

export default SignUp;