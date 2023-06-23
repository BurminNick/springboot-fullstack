import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";

const AddIcon = ()=> "+";
const CloseIcon = ()=> "-";

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) =>{
    const { isOpen, onOpen, onClose } = useDisclosure()
    return <>
        <Button
            bg={"darkgreen"}
            color={"white"}
            rounded={"full"}
            onClick={onOpen}
        >
            Update
        </Button>
        <Drawer isOpen={isOpen} onClose={onClose}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Update customer</DrawerHeader>

                <DrawerBody>
                    <UpdateCustomerForm
                        fetchCustomers = {fetchCustomers}
                        initialValues = {initialValues}
                        customerId={customerId}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button
                        leftIcon={<CloseIcon/>}
                        colorScheme={"teal"}
                        onClick={onClose}
                    >
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
        </>
}

export default UpdateCustomerDrawer;