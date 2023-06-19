const UserProfile = ({name, age, gender, imageNumber, ...props}) =>{

    gender = gender === "male"?"men":"women"

    return (
        <div>
            <p>{name}</p>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/${imageNumber}.jpg`}/>
            {props.children}
        </div>
    )
}
export default UserProfile;