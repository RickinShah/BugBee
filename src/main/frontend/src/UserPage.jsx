import logoo from './assets/assets/man.png'
import Search from './assets/assets/search.png'
import add from './assets/assets/add.png'
import pencil from './assets/assets/pencil.png'
import Card from './Cards.jsx';
import { Link } from 'react-router-dom'
import { useEffect, useState, useRef } from 'react'

const UserPage = () => {


    const [username, setUsername] = useState(localStorage.getItem('name'));
    const [posts, setPosts] = useState([]);
    const [lastId, setLastId] = useState("9223372036854775807");
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [SearchToggle,setSearchToggle] = useState(false)
    const [searchResults, setSearchResults] = useState([]);


    const searchKeyword = async (e) => {
        const offset = 0;
        const size = 10;
        const keyword = e.target.value;
        try {
            keyword.trim() === '' ? setSearchToggle(false) : setSearchToggle(true);
            if(SearchToggle) {
                const response = await fetch(`/api/search?keyword=${keyword}&size=${size}&offset=${offset}`);
                const searchData = await response.json();
                if (searchData.length === 0) {
                    setSearchResults([]);
                } else {
                    // console.log(searchData);
                    setSearchResults(searchData);
                }
            }
        } catch (error) {
            console.log('Search Result Error: ', error);
        }
    }

    const fetchPosts = async () => {
        if (isLoading) return;
        setIsLoading(true);
        const size = 3;
        try {
            const response = await fetch(`/api/posts?lastId=${lastId}&size=${size}`);
            const res = await response.json();
            if (res.length === 0) {
                setHasMore(false);
            } else {
                setPosts([...posts, ...res]);
                setLastId(res[res.length - 1].spostId);
            }
            // console.log(res);
        } catch (error) {
            console.log(`Error: ${error}`);
        } finally {
            setIsLoading(false);
        }
    }
    useEffect(() => {
        fetchPosts();
    }, []);

    const postsContainerRef = useRef(null);

    const checkScroll = () => {
        const { bottom } = postsContainerRef.current.getBoundingClientRect();
        const windowHeight = window.innerHeight;

        if (bottom <= windowHeight + 100 && hasMore && !isLoading) {
            fetchPosts();
        }
    };

    useEffect(() => {
        window.addEventListener('scroll', checkScroll);
        return () => {
            window.removeEventListener('scroll', checkScroll);
        }
    }, [hasMore, !isLoading])

    return (
        <div className="bg-[#0f0f0f] w-full h-screen flex ">


            <div className="bg-[#2C2C2C] w-1/4 h-screen fixed ">
                <div className='absolute top-6'>
                    <div className='mx-3 '>
                        <div className='flex justify-center '>
                            <img src={localStorage.getItem("profilePic")} alt="pfp" className='w-1/3 h-1/3 ' />

                        </div>
                        <div className='flex justify-center my-3 font-semibold'>
                            {username}
                            <Link to="/Accounts">
                                <button className='w-5 h-5 bg-yellow-400 rounded-full hover:bg-yellow-600 mx-1 p-1'>
                                    <img src={pencil} alt="pencil" />
                                </button>
                            </Link>
                        </div>
                    </div>

                    <div className=' w-full screen my-24'>
                        <div className='flex-col'>
                            <div className='bg-gray-800 w-full h-14 ab my-2'>
                                <Link to="/accounts">
                                    <button className='bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]'>Account</button>
                                </Link>
                            </div>
                            <div className='bg-gray-800 w-full h-14 ab my-2'>
                                <button className='bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]'>FAQs</button>
                            </div>
                            <div className='bg-gray-800 w-full h-14 ab my-2'>
                                <button className='bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-yellow-400 hover:text-[#3F3F3F]'>Help</button>
                            </div>
                            <div className='bg-gray-800 w-full h-14 ab my-2 '>
                                <button className='bg-[#3F3F3F] w-full h-full text-yellow-400 font-bold hover:bg-[#fa3838] hover:text-[#ffffff]'>Sign-Out</button>
                            </div>





                        </div>

                    </div>
                </div>

            </div>


            <div className='absolute right-0 w-3/4'>

                <div className='fixed right-0 w-3/4'>

                    <nav className='bg-[#1D1D1D]  h-12 rounded-lg m-3 flex items-center justify-start '>
                        <div className='w-full h-full flex'>
                            <div className='w-2/12 h-full flex items-center justify-center'>
                                <img src={localStorage.getItem("profilePic")} alt="" className='w-12 h-12 m-1' />
                            </div>
                            <div className='w-8/12 h-full flex items-center justify-center'>
                                <input type="text" name='searchUser' className='bg-[#D9D9D9] w-full h-9 rounded-2xl
                                text-yellow-500 px-12 font-bold
                                underline' onChange={searchKeyword}/>
                                <div
                                    className={`${SearchToggle ? '' : 'hidden'} absolute w-[720px] h-fit top-14 bg-[#D9D9D9]`}>
                                    <ul className='m-1 p-1 text-lg font-bold '>
                                        {searchResults.map((result, index) => (
                                            <li className='border m-1 bg-white text-yellow-400 font-bold text-center'>
                                        {result.username}
                                            </li>
                                            ))
                                        }
                                    </ul>
                                </div>
                                <button className='w-7 h-7 rounded-full fixed left-[585px]'>
                                    <img src={Search} alt="search" className='w-7 h-7'/>
                                </button>
                            </div>
                            <div className='w-2/12 h-full flex items-center justify-center'>
                                <Link to="/NewPost">
                                    <button
                                        className='bg-black rounded-full w-10 h-10 mx-5 m-1 flex justify-center align-middle p-1.5'>
                                        <img src={add} alt="add" className='w-7 h-7 '/></button>
                                </Link>
                            </div>
                        </div>
                    </nav>
                </div>


                <div ref={postsContainerRef} className='static top-20 m-2'>
                    <br /><br />
                    {posts.map((post, index) => (
                        <Card
                            key={index}
                            post={post}
                        />
                    ))}
                </div>

            </div>

        </div>
    )
}

export default UserPage;
