/** geb.env will trump this **/
driver = determineDriver()

def determineDriver() {
    return {
        println("Should never get here!")
    }
}

environments {
    grid_firefox {
        driver = {
            println("Loading the correct web driver!")
        }
    }
}


