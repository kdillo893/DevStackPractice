"use client";
import { PlusIcon, TrashIcon } from "@heroicons/react/24/outline";
import { Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";
import axios from "axios";
import { useEffect, useState } from "react";

function UserRow({ uid, first_name, last_name, email }) {

    //I should have a function in here that I can pass that would be called to delete in the
    const handleDeleteUser = (e) => {
        //this will just use uid present for this row.
        //e is event from pushing button...

        e.preventDefault(true);
        const answer = window.confirm(`Do you want to delete ${uid}?`);
        if (answer)
            axios.delete(`/api/users/${uid}`)
                .then((response) => {
                    Router.reload();
                }).catch((error) => {
                    console.log(error);
                    alert(error);
                });

    };

    return (
        <TableRow>
            <TableCell>{uid}</TableCell>
            <TableCell>{first_name}</TableCell>
            <TableCell>{last_name}</TableCell>
            <TableCell>{email}</TableCell>
            <TableCell>TBD</TableCell>
            <TableCell>
                <Button action={handleDeleteUser} >
                    <TrashIcon height={16} />
                </Button>
            </TableCell>
        </TableRow>
    )
}

export default function Users() {
    const [users, setUsers] = useState([]);
    const [usersLoaded, setUsersLoaded] = useState(false);
    const [newUser, setNewUser] = useState({
        "first_name": null,
        "last_name": null,
        "email": null,
    });

    //this happens on initial load (basically componentDidMount)
    useEffect(() => {
        if (users == null || users.length == 0)
            axios.get('/api/users/')
                .then((response) => {
                    //response has "success" which is an array with one object.
                    const data = response.data.success[0].data;

                    if (data != null) setUsers(data.users);

                    setUsersLoaded(true);
                }).catch((error) => {
                    console.log(error);
                });
    });


    if (!usersLoaded) return <p>Loading ...</p>;
    if (!users || users.length == 0) return <p>No users</p>;

    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>UID</TableCell>
                        <TableCell>First Name</TableCell>
                        <TableCell>Last Name</TableCell>
                        <TableCell>Email</TableCell>
                        <TableCell>Notes</TableCell>
                        <TableCell>Delete?</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        users?.map((aUser, idx) => {
                            return <UserRow key={idx} {...aUser} />
                        })
                    }
                    <TableRow>
                        <TableCell><PlusIcon height={16} /></TableCell>
                        <TableCell />
                        <TableCell />
                        <TableCell />
                        <TableCell />
                    </TableRow>
                </TableBody>
            </Table>
        </TableContainer>
    );
}
