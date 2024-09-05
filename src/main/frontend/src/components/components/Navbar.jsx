import { useEffect, useState } from "react"
import React from 'react'
import { Link, NavLink } from "react-router-dom"

// import { styles } from "../../styles"

// import { navLinks } from "../../constants"

// import {logo , menu , close} from "../../assets/assets";
import logoo from "../../assets/assets/logoo.png"



const Navbar = () => {
  const [active, setActive] = useState("")
  return (
    <nav className="${styles.paddingx} w-full flex item-center py-5 fixed top-0 z-20 bg-primary">
      <div className="w-full flex justify-between items-center max-w-7xl mx-auto">
        <Link
          to="/"
          className="flex item-center gap-2"
          onClick={() => {
            setActive("");
            window.scrollTo(0,0);
          }}
        >
          <img src={logoo} alt="logo" className="w-9 h-9 object-contain" />
          <p className="text-white text-[18px] font-bold cursor-pointer">Shubham <span className="sm:block hidden">| Patel</span></p>
        </Link>
      

      </div>
    </nav>
  )
}

export default Navbar