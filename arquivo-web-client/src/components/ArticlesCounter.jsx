import { useEffect, useState } from "react";

function ArticlesCounter({ entityId }) {
    const [counter, setPosts] = useState([]);

    useEffect(() => {
        fetch("http://127.0.0.1:8082/article/count/" + entityId)
            .then((res) => res.json())
            .then((res) => {
                setPosts(res);
            });
    }, []);

    return (
        <span>{counter}</span>
    );

};

export default ArticlesCounter;