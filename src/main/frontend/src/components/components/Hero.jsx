import {ComputersCanvas} from "./canvas";

const Hero = () => {
    return (
        <section className="relative w-full h-screen mx-auto">
            <div
                className={'${styles.paddingX} absolute inset-0 top-[120px] max-w-7xl mx-auto flex flex-roww items-start gap-5'}>
                <div className="flex flex-col justify-center items-center mt-5">

                </div>

            </div>
            <ComputersCanvas/>
        </section>
    )
}

export default Hero