import {Route, Routes} from "react-router-dom";
import BoardList from "./features/boards/BoardList.tsx";
import BoardPage from "./features/boards/BoardPage.tsx";

function App() {

  return (
    <>
        <h1>Kanban Board App</h1>
        <main>
            <Routes>
                <Route path="/" element={<BoardList />} />
                <Route path="/boards/:boardId" element={<BoardPage />} />
            </Routes>
        </main>
    </>
  )
}

export default App
